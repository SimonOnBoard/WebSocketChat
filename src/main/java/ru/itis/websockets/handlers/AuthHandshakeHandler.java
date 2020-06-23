
package ru.itis.websockets.handlers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.util.WebUtils;
import ru.itis.websockets.orm.User;
import ru.itis.websockets.repositories.UsersRepository;

import javax.servlet.http.Cookie;
import java.util.Map;
import java.util.Optional;

@Component
public class AuthHandshakeHandler implements HandshakeHandler {

    @Value("${jwt.secret}")
    private String secret;

    private DefaultHandshakeHandler defaultHandshakeHandler = new DefaultHandshakeHandler();
    private SecurityHandler handler;
    private UsersRepository usersRepository;

    public AuthHandshakeHandler(SecurityHandler handler, UsersRepository usersRepository) {
        this.handler = handler;
        this.usersRepository = usersRepository;
    }

    @Override
    public boolean doHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws HandshakeFailureException {
        ServletServerHttpRequest request = (ServletServerHttpRequest) serverHttpRequest;
        Cookie cookie = WebUtils.getCookie(request.getServletRequest(), "auth");
        String URI = serverHttpRequest.getURI().toString();
        //Пропускаем сразу если это регистрация или авторизация
        boolean found = false;
        if (URI.contains("registration")) {
            return defaultHandshakeHandler.doHandshake(serverHttpRequest, serverHttpResponse, webSocketHandler, map);
        }
        //Чекаем куку
        if (cookie != null) {
            try {
                 Claims claims;
                claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(cookie.getValue()).getBody();
                Long id = Long.parseLong(claims.get("sub", String.class));
                Optional<User> userCandidate = usersRepository.findById(id);
                if (userCandidate.isPresent()) {
                    map.put("user", userCandidate.get());
                    found = true;
                    System.out.println("User found");
                }
            } catch (Exception e) {
                //Если возникла ошибка при парсе куки мы перейдём в конец метода
                System.out.println(e.getMessage());
            }

        }
        if (URI.contains("chat") && !found) {
            webSocketHandler = handler;
            System.out.println("CloseHandler was set");
        }
        return defaultHandshakeHandler.doHandshake(serverHttpRequest, serverHttpResponse, webSocketHandler, map);
    }
}