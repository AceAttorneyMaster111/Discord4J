/*
 *     This file is part of Discord4J.
 *
 *     Discord4J is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Discord4J is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Discord4J.  If not, see <http://www.gnu.org/licenses/>.
 */

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

/**
 * A Java logging handler to report events to a specific {@link IChannel}. Uses an embed by default.
 * Use by adding an instance of this class to a {@link java.util.logging.Logger}.
 * 
 * @see java.util.logging
 */
public class DiscordLoggingHandler extends Handler {
	/**
	 * The channel which the handler will send logs to.
	 */
	private IChannel channel;
	/**
	 * The default color map stored in {@link #colors}.
	 */
	public static final Map<Level, Color> DEFAULT_COLORS = makeDefaultColorsMap();
	/**
	 * A method to create the map used in {@link #DEFAULT_COLORS}.
	 * @return The created map.
	 */
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
	/**
	 * The set of colors that are used for the embed, corresponding to the {@link #Level} of the logged event.
	 */
	private Map<Level, Color> colors;
	
	/**
	 * Get the map used to choose the embed color.
	 * 
	 * @return The current value of {@link #colors}.
	 */
	public Map<Level, Color> getColors() {
		return this.colors;
	}
	
	/**
	 * Set the map used to choose the embed color.
	 * 
	 * @param colors The map that will be used.
	 */
	public void setColors(Map<Level, Color> colors) {
		this.colors = colors;
	}
	
	/**
	 * Get the destination channel for logging messages.
	 * 
	 * @return The current value of {@link #channel}.
	 */
	public IChannel getChannel() {
		return this.channel;
	}
	
	/**
	 * Set the destination channel for logging messages.
	 * 
	 * @param channel The channel that will be used.
	 */
	public void setChannel(IChannel channel) {
		this.channel = channel;
	}
	
	/**
	 * Creates a new handler set to send messages to the given channel.
	 * 
	 * @param channel The initial destination channel for logging messages.
	 */
	public DiscordLoggingHandler(IChannel channel) {
		this.channel = channel;
		this.colors = DEFAULT_COLORS;
	}
	
	/* (non-Javadoc)
	 * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
	 */
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

	/* (non-Javadoc)
	 * @see java.util.logging.Handler#flush()
	 */
	@Override
	public void flush() {
	}

	/* (non-Javadoc)
	 * @see java.util.logging.Handler#close()
	 */
	@Override
	public void close() {
	}
	
	/* (non-Javadoc)
	 * @see java.util.logging.Handler#isLoggable(java.util.logging.LogRecord)
	 */
	@Override
	public boolean isLoggable(LogRecord record) {
		if (record.getLevel().getName().length() > EmbedBuilder.TITLE_LENGTH_LIMIT ||
		(record.getSourceClassName() + record.getSourceMethodName() + record.getMessage()).length() + 3 > EmbedBuilder.DESCRIPTION_CONTENT_LIMIT) {
			return false;
		}
		return super.isLoggable(record);
	}

}
