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
 * @author Noah Simon
 *
 */
public class DiscordLoggingHandler extends Handler {
	/**
	 * 
	 */
	private IChannel channel;
	/**
	 * 
	 */
	public static final Map<Level, Color> DEFAULT_COLORS = makeDefaultColorsMap();
	/**
	 * @return
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
	 * 
	 */
	private Map<Level, Color> colors;
	
	/**
	 * @return
	 */
	public Map<Level, Color> getColors() {
		return this.colors;
	}
	
	/**
	 * @param colors
	 */
	public void setColors(Map<Level, Color> colors) {
		this.colors = colors;
	}
	
	/**
	 * @return
	 */
	public IChannel getChannel() {
		return this.channel;
	}
	
	/**
	 * @param channel
	 */
	public void setChannel(IChannel channel) {
		this.channel = channel;
	}

	/**
	 * 
	 */
	public DiscordLoggingHandler() {
		this.colors = DEFAULT_COLORS;
	}
	
	/**
	 * @param channel
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
