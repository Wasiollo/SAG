package com.sag.pagent.manager.hierarchy;

import com.sag.pagent.shop.articles.ArticleType;
import jade.core.AID;
import lombok.Data;

import java.io.Serializable;

@Data
public class BrokerHierarchyKey implements Serializable {
    private final AID broker;
    private final ArticleType type;

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BrokerHierarchyKey other = (BrokerHierarchyKey) obj;
        return broker == other.broker;
    }
}
