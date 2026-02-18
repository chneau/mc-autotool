package chneau.autotool;

public class Safe {
	public static <T> T call(String name, java.util.function.Supplier<T> supplier, T defaultValue) {
		try {
			return supplier.get();
		} catch (Throwable t) {
			Main.LOGGER.error("Exception in " + name, t);
			Util.chatError("[mc-autotool] Error in " + name + ": " + t.getMessage());
			return defaultValue;
		}
	}

	public static void run(String name, Runnable runnable) {
		try {
			runnable.run();
		} catch (Throwable t) {
			Main.LOGGER.error("Exception in " + name, t);
			Util.chatError("[mc-autotool] Error in " + name + ": " + t.getMessage());
		}
	}
}
