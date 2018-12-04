/**
 * 
 */
package com.vict.netty.config;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author DELL
 *
 */
public class NettyConfiguration {
        public  static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
}
