package br.com.echovita.application;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class EchoVitaLog {

    private static final String PREFIXO = "[EchoVita] ";
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final Logger LOGGER = Logger.getLogger("EchoVita");
    private static final List<Consumer<String>> LISTENERS = new ArrayList<>();

    private EchoVitaLog() {
    }

    public static void inicializar() {
        LOGGER.setUseParentHandlers(false);
        for (Handler handler : LOGGER.getHandlers()) {
            LOGGER.removeHandler(handler);
        }

        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        handler.setFormatter(new java.util.logging.Formatter() {
            @Override
            public String format(java.util.logging.LogRecord record) {
                return PREFIXO + record.getMessage() + System.lineSeparator();
            }
        });

        LOGGER.addHandler(handler);
        LOGGER.setLevel(Level.ALL);
    }

    public static void registrarListener(Consumer<String> listener) {
        if (listener != null) {
            LISTENERS.add(listener);
        }
    }

    public static void acao(String mensagem) {
        registrar(Level.INFO, mensagem);
    }

    public static void erro(String mensagem) {
        registrar(Level.WARNING, mensagem);
    }

    private static void registrar(Level nivel, String mensagem) {
        String linha = "[" + LocalTime.now().format(FORMATO_HORA) + "] " + mensagem;
        LOGGER.log(nivel, linha);

        for (Consumer<String> listener : LISTENERS) {
            listener.accept(linha);
        }
    }
}
