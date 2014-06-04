package io.dahuapp.driver;

import java.util.ArrayList;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

/**
 * Keyboard driver.
 *
 * Note: Keyboard driver must not make use of User Interface.
 * Keyboard driver's methods must only concern low level
 * functionalities. User interactions must be implemented in Kernel modules.
 */
public class KeyboardDriver {

    /**
     * Listeners for this driver.
     */
    public interface KeyboardListener {
        public void keyTyped(int keyCode, final String keyName);
        public void keyPressed(int keyCode, final String keyName);
        public void keyReleased(int keyCode, final String keyName);
    }

    /**
     * List of listeners.
     */
    private static ArrayList<KeyboardListener> listeners = new ArrayList<>();

    /**
     * Add a listener to this driver.
     *
     * @param listener Listener to add to this keyboard driver.
     *                 Note : a listener is a javascript event that is
     *                        triggered when an event on a key is detected.
     */
    public static void addKeyListener(KeyboardListener listener) {
        listeners.add(listener);
    }

    public static void onLoad() {
        // register the native hook.
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            LoggerDriver.error("Keyboard Driver load",
                    "There was a problem registering the native hook. "
                            + ex.getMessage(), ex);
            System.exit(1);
        }

        // initialize native key listener.
        GlobalScreen.getInstance().addNativeKeyListener(new NativeKeyListener() {
            @Override
            public void nativeKeyReleased(NativeKeyEvent nke) {
                for (final KeyboardDriver.KeyboardListener listener : listeners) {
                    listener.keyReleased(nke.getKeyCode(), NativeKeyEvent.getKeyText(nke.getKeyCode()));
                }
            }

            @Override
            public void nativeKeyTyped(NativeKeyEvent nke) {
                for (final KeyboardListener listener : listeners) {
                    listener.keyTyped(nke.getKeyCode(), NativeKeyEvent.getKeyText(nke.getKeyCode()));
                }
            }

            @Override
            public void nativeKeyPressed(NativeKeyEvent nke) {
                for (final KeyboardListener listener : listeners) {
                    listener.keyPressed(nke.getKeyCode(), NativeKeyEvent.getKeyText(nke.getKeyCode()));
                }
            }
        });
    }
}