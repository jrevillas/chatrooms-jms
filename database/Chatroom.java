package database;

import java.sql.Timestamp;

public class Chatroom {

    private int id;
    private String name;
    private String handle_creator;
    private Timestamp create_date;

    public Chatroom() {}

    public int getId() { return id; }

    public String getName() { return name; }

    public Chatroom setName(String name) { this.name = name; return this; }

    public String getHandle_creator() { return handle_creator; }

    public Chatroom setHandle_creator(String handle_creator) { this.handle_creator = handle_creator; return this; }

    public Timestamp getCreate_date() { return create_date; }

    public Chatroom setCreate_date(Timestamp create_date) { this.create_date = create_date; return this; }

    public Chatroom setId(int id) { this.id = id; return this; }
}
