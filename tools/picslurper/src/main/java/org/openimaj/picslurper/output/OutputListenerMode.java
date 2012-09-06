package org.openimaj.picslurper.output;

import org.kohsuke.args4j.CmdLineOptionsProvider;

/**
 * The various {@link OutputListener} modes for the command line tool
 * @author Sina Samangooei (ss@ecs.soton.ac.uk)
 *
 */
public enum OutputListenerMode implements CmdLineOptionsProvider {
	/**
	 * an {@link OutputListener} which starts a Zmq publisher
	 */
	ZMQ {
		@Override
		public OutputListener getOptions() {
			return new ZMQOutputListener();
		}
	}
	;

	@Override
	public abstract OutputListener getOptions() ;

}
