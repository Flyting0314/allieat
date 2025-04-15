package com.storeOrder.handler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class OrderWebSocketHandler extends TextWebSocketHandler {

	private final Map<Integer, List<WebSocketSession>> storeSessions = new ConcurrentHashMap<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		Integer storeId = getStoreIdFromQueryParam(session);
		if (storeId != null) {
			storeSessions.computeIfAbsent(storeId, k -> new CopyOnWriteArrayList<>()).add(session);
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		Integer storeId = getStoreIdFromQueryParam(session);
		if (storeId != null) {
			storeSessions.getOrDefault(storeId, List.of()).remove(session);
		}
	}

	public void sendOrderToStore(Integer storeId, String message) throws IOException {
		List<WebSocketSession> sessions = storeSessions.get(storeId);
		if (sessions != null) {
			for (WebSocketSession session : sessions) {
				if (session.isOpen()) {
					session.sendMessage(new TextMessage(message));
				}
			}
		}
	}

	private Integer getStoreIdFromQueryParam(WebSocketSession session) {
		String query = session.getUri().getQuery();
		if (query != null && query.contains("storeId")) {
			for (String part : query.split("&")) {
				if (part.startsWith("storeId=")) {
					return Integer.valueOf(part.replace("storeId=", ""));
				}
			}
		}
		return null;
	}
}
