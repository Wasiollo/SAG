package com.sag.pagent.broker.behaviours;

import com.sag.pagent.behaviors.HandleManyResponds;
import com.sag.pagent.behaviors.MyTickerBehaviour;
import com.sag.pagent.behaviors.ReceiveMessagesBehaviour;
import com.sag.pagent.messages.MessagesUtils;
import com.sag.pagent.shop.messages.ArticlesStatusQuery;
import com.sag.pagent.shop.messages.ArticlesStatusReply;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class QueryShopsBehaviour extends MyTickerBehaviour {
    private ReceiveMessagesBehaviour receiveMessages;
    private QueryShopsBehaviourListener listener;
    private HashSet<AID> shopAgents;

    public QueryShopsBehaviour(Agent a, long timeout, ReceiveMessagesBehaviour receiveMessages, QueryShopsBehaviourListener listener) {
        super(a, timeout);
        this.receiveMessages = receiveMessages;
        this.listener = listener;
        shopAgents = new HashSet<>();
    }

    @Override
    protected void onTick() {
        try {
            log.debug("send ArticlesStatusQuery to {}", getShopAgentsName());
            ACLMessage msg = MessagesUtils.createMessage(ACLMessage.REQUEST);
            shopAgents.forEach(msg::addReceiver);
            msg.setContentObject(new ArticlesStatusQuery());
            myAgent.send(msg);
            receiveMessages.registerRespond(new HandleArticlesStatusReplies(myAgent, msg));
        } catch (IOException ex) {
            log.error("IOException in sendArticlesStatusQuery", ex);
        }
    }

    @NotNull
    private List<String> getShopAgentsName() {
        return shopAgents.stream().map(AID::getLocalName).collect(Collectors.toList());
    }

    public void addShopAgent(AID shopAgent) {
        shopAgents.add(shopAgent);
    }

    public interface QueryShopsBehaviourListener extends Serializable {
        void onArticlesStatusReply(ACLMessage msg, ArticlesStatusReply articlesStatusReply) throws UnreadableException;
    }

    private class HandleArticlesStatusReplies extends HandleManyResponds {
        HandleArticlesStatusReplies(Agent myAgent, ACLMessage sendMessage) {
            super(myAgent, sendMessage);
        }

        @Override
        protected void repeatAction(ACLMessage msg) throws UnreadableException {
            log.debug("get message {} from {}", ArticlesStatusReply.class.getSimpleName(), msg.getSender().getLocalName());
            ArticlesStatusReply articlesStatusReply = (ArticlesStatusReply) msg.getContentObject();
            listener.onArticlesStatusReply(msg, articlesStatusReply);
        }
    }
}
