# pagent

Pagent - Purchasing agent
Project for SAG (Agent Systems)
Topic: Distributed purchasing system for taking care of supplies in the company.

## setup
* Import from VCS
* Po lewej w plikach pom.xml -> right-click -> Maven -> Reimport
* Powinno pobrać Jade’a i wszystko co trzeba
* Konfiguracja projektu Lombok https://www.baeldung.com/lombok-ide

## uruchomienie:
### ręczne:
* Start z zielonej szczałki i powinno wyskoczyć okienko.  
W którym po zaznaczeniu Main-Container można dać Actions->Start New Agent i wystartować napisanego przez nas agenta, możemy odpalić DummyAgenta, którym możemy wstrzykiwać wiadomości, oraz odpalić IntrospectorAgent, w którym możemy right-click na agenta, debug On i wyświetlić okienko z jego Behaviorami i listą wiadomości ACL które czekają/odebrał/czekają na wysłanie/wysłał, do debugowania zajebiste

### automatyczne
* Można dodać auto uruchamianie StartAgent wraz z startem całego programu.
  * Wejść w Run/Debug Configuration (obok zielonej strzalki do uruchamiania rozwinąć listę i wybrać Edit Configuration...)
  * w polu Program arguments wpisać `-gui -agents StartAgent:com.sag.pagent.agents.StartAgent`
