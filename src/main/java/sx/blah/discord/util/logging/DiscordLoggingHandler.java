package sx.blah.discord.util.logging;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Level;
import java.util.Map;
import java.util.HashMap;
import java.awt.Color;

public class DiscordLoggingHandler extends Handler {
	private IChannel channel;
	public static final Map<Level, Color> DEFAULT_COLORS = makeDefaultColorsMap();
	private static Map<Level, Color> makeDefaultColorsMap() {
		Map<Level, Color> temp = new HashMap<Level, Color>();
		temp.put(Level.SEVERE, Color.RED);
		temp.put(Level.WARNING, Color.ORANGE);
		temp.put(Level.INFO, Color.YELLOW);
		temp.put(Level.CONFIG, Color.GREEN);
		temp.put(Level.FINE, Color.CYAN);
		temp.put(Level.FINER, Color.BLUE);
		temp.put(Level.FINEST, Color.MAGENTA);
		return temp;
	}
	private Map<Level, Color> colors;
	
	public Map<Level, Color> getColors() {
		return this.colors;
	}
	
	public void setColors(Map<Level, Color> colors) {
		this.colors = colors;
	}
	
	public IChannel getChannel() {
		return this.channel;
	}
	
	public void setChannel(IChannel channel) {
		this.channel = channel;
	}

	public DiscordLoggingHandler() {
		this.colors = DEFAULT_COLORS;
	}
	
	public DiscordLoggingHandler(IChannel channel) {
		this.channel = channel;
		this.colors = DEFAULT_COLORS;
	}
	
	@Override
	public void publish(LogRecord record) {
		if (!isLoggable(record)) {
			return;
		}
		EmbedBuilder builder = new EmbedBuilder()
		.withTitle(record.getLevel().getName())
		.withDescription(record.getSourceClassName() + "#" + record.getSourceMethodName() + ":\n" + record.getMessage())
		.withColor(this.colors.containsKey(record.getLevel()) ? this.colors.get(record.getLevel()) : Color.WHITE)
		.withTimestamp(record.getMillis());
		RequestBuffer.request(() -> {
			this.channel.sendMessage(builder.build());
		});
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() {
	}
	
	@Override
	public boolean isLoggable(LogRecord record) {
		if (record.getLevel().getName().length() > EmbedBuilder.TITLE_LENGTH_LIMIT ||
		(record.getSourceClassName() + record.getSourceMethodName() + record.getMessage()).length() + 3 > EmbedBuilder.DESCRIPTION_CONTENT_LIMIT) {
			return false;
		}
		return super.isLoggable(record);
	}

}
