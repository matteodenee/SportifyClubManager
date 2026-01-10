package communication.network;

import com.sportify.manager.persistence.AbstractFactory;

public final class ChatServerApp {
    public static void main(String[] args) throws Exception {
        int port = 5555;

        AbstractFactory f = AbstractFactory.createFactory();
        var convDao = f.createConversationDAO();
        var msgDao = f.createMessageDAO();

        ConversationService service = new ConversationService(convDao, msgDao);

        ChatServer server = new ChatServer(port);
        server.addObserver(new ChatServerObserver(server, service));

        System.out.println("ChatServer started on port " + port);
        server.listen();
    }
}
