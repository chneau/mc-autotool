package chneau.autotool;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
public class Throttler {
	private static int globalTicks = 0;
	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> globalTicks++);
	}
	/**
	 * Staggers tasks based on the owner's hash code so that different modules run
	 * their logic on different ticks, even if they use the same interval.
	 *
	 * @param owner
	 *            The object requesting the check (usually 'this').
	 * @param interval
	 *            How many ticks to wait between runs.
	 * @return true if the task should run this tick.
	 */
	public static boolean shouldRun(Object owner, int interval) {
		if (interval <= 1)
			return true;
		int offset = Math.abs(owner.hashCode()) % interval;
		return (globalTicks + offset) % interval == 0;
	}
}
