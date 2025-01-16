package org.example.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.database.MessangerDao;
import org.example.entities.Message;
import org.example.pojo.Mapper;
import org.example.pojo.MessageDto;
import org.example.pojo.MessagesResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/messages")
public class MessagesServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MessangerDao dao = new MessangerDao();
    private final Mapper mapper = new Mapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=utf-8");
        setCORS(resp);

        List<MessageDto> dtos = mapper.mapListMessageToDto(dao.getAllMessagesForChat(1));

        String responseString = objectMapper.writeValueAsString(new MessagesResponse(dtos));
        System.out.println("Response:");
        System.out.println(responseString);

        resp.setStatus(200);
        PrintWriter pw = resp.getWriter();
        pw.write(responseString);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCORS(resp);
        resp.setHeader("Accept", "application/json");
        resp.setStatus(201);

        try (BufferedInputStream bis = new BufferedInputStream(req.getInputStream())) {
            Message message = objectMapper.readValue(bis, Message.class);
            System.out.println("Post message: " + message.toString());
            dao.saveMessage(message);
            WebSocketConnection.notifyChat();
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setCORS(resp);
        resp.setContentType("application/json; charset=utf-8");
        resp.setStatus(204);
    }

    private void setCORS(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
