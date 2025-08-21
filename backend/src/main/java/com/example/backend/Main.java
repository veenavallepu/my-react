package com.example.backend;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Main {
	public static void main(String[] args) throws IOException {
		int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

		server.createContext("/api/health", new JsonHandler(200, "{\"status\":\"ok\"}"));
		server.createContext("/api/greeting", exchange -> {
			if (!"GET".equals(exchange.getRequestMethod())) {
				exchange.sendResponseHeaders(405, -1);
				return;
			}
			String name = queryParams(exchange).getOrDefault("name", "World");
			String body = String.format("{\"message\":\"Hello, %s!\"}", escapeJson(name));
			writeJson(exchange, 200, body);
		});

		server.start();
		System.out.println("Server started on port " + port);
	}

	private static Map<String, String> queryParams(HttpExchange exchange) {
		String raw = exchange.getRequestURI().getQuery();
		return java.util.Arrays.stream(raw == null ? new String[]{} : raw.split("&"))
				.map(kv -> kv.split("=", 2))
				.collect(java.util.stream.Collectors.toMap(
						arr -> urlDecode(arr[0]),
						arr -> arr.length > 1 ? urlDecode(arr[1]) : "",
						(a, b) -> b
				));
	}

	private static String urlDecode(String s) {
		try {
			return java.net.URLDecoder.decode(s, StandardCharsets.UTF_8);
		} catch (Exception e) {
			return s;
		}
	}

	private static void writeJson(HttpExchange exchange, int status, String body) throws IOException {
		byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
		exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
		exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,OPTIONS");
		exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
		exchange.sendResponseHeaders(status, bytes.length);
		try (OutputStream os = exchange.getResponseBody()) {
			os.write(bytes);
		}
	}

	private static String escapeJson(String s) {
		return s.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	private static class JsonHandler implements HttpHandler {
		private final int status;
		private final String body;

		JsonHandler(int status, String body) {
			this.status = status;
			this.body = body;
		}

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			if ("OPTIONS".equals(exchange.getRequestMethod())) {
				exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
				exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,OPTIONS");
				exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
				exchange.sendResponseHeaders(204, -1);
				return;
			}
			if (!"GET".equals(exchange.getRequestMethod())) {
				exchange.sendResponseHeaders(405, -1);
				return;
			}
			writeJson(exchange, status, body);
		}
	}
}