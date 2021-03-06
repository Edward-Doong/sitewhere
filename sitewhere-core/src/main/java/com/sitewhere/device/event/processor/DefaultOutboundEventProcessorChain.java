/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.event.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.processor.IOutboundEventProcessor;
import com.sitewhere.spi.device.event.processor.IOutboundEventProcessorChain;

/**
 * Default implementation of {@link IOutboundEventProcessorChain} interface.
 * 
 * @author Derek
 */
public class DefaultOutboundEventProcessorChain implements IOutboundEventProcessorChain {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DefaultOutboundEventProcessorChain.class);

	/** Indicates whether processing is enabled */
	private boolean processingEnabled = false;

	/** List of event processors */
	private List<IOutboundEventProcessor> processors = new ArrayList<IOutboundEventProcessor>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		LOGGER.info("Outbound event processor chain starting...");
		for (IOutboundEventProcessor processor : getProcessors()) {
			processor.start();
		}
		LOGGER.info("Outbound event processor chain started.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		LOGGER.info("Outbound event processor chain stopping...");
		for (IOutboundEventProcessor processor : getProcessors()) {
			processor.stop();
		}
		LOGGER.info("Outbound event processor chain stopped.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.processor.IOutboundEventProcessorChain#
	 * setProcessingEnabled(boolean)
	 */
	@Override
	public void setProcessingEnabled(boolean enabled) {
		this.processingEnabled = enabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.processor.IOutboundEventProcessorChain#
	 * isProcessingEnabled()
	 */
	@Override
	public boolean isProcessingEnabled() {
		return processingEnabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#onMeasurements
	 * (com.sitewhere.spi.device.event.IDeviceMeasurements)
	 */
	@Override
	public void onMeasurements(IDeviceMeasurements measurements) throws SiteWhereException {
		if (isProcessingEnabled()) {
			for (IOutboundEventProcessor processor : getProcessors()) {
				try {
					processor.onMeasurements(measurements);
				} catch (SiteWhereException e) {
					LOGGER.error(e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#onLocation(com
	 * .sitewhere.spi.device.event.IDeviceLocation)
	 */
	@Override
	public void onLocation(IDeviceLocation location) throws SiteWhereException {
		if (isProcessingEnabled()) {
			for (IOutboundEventProcessor processor : getProcessors()) {
				try {
					processor.onLocation(location);
				} catch (SiteWhereException e) {
					LOGGER.error(e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#onAlert(com.sitewhere
	 * .spi.device.event.IDeviceAlert)
	 */
	@Override
	public void onAlert(IDeviceAlert alert) throws SiteWhereException {
		if (isProcessingEnabled()) {
			for (IOutboundEventProcessor processor : getProcessors()) {
				try {
					processor.onAlert(alert);
				} catch (SiteWhereException e) {
					LOGGER.error(e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#onCommandInvocation
	 * (com.sitewhere.spi.device.event.IDeviceCommandInvocation)
	 */
	@Override
	public void onCommandInvocation(IDeviceCommandInvocation invocation) throws SiteWhereException {
		if (isProcessingEnabled()) {
			for (IOutboundEventProcessor processor : getProcessors()) {
				try {
					processor.onCommandInvocation(invocation);
				} catch (SiteWhereException e) {
					LOGGER.error(e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#onCommandResponse
	 * (com.sitewhere.spi.device.event.IDeviceCommandResponse)
	 */
	@Override
	public void onCommandResponse(IDeviceCommandResponse response) throws SiteWhereException {
		if (isProcessingEnabled()) {
			for (IOutboundEventProcessor processor : getProcessors()) {
				try {
					processor.onCommandResponse(response);
				} catch (SiteWhereException e) {
					LOGGER.error(e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessorChain#getProcessors
	 * ()
	 */
	@Override
	public List<IOutboundEventProcessor> getProcessors() {
		return processors;
	}

	public void setProcessors(List<IOutboundEventProcessor> processors) {
		this.processors = processors;
	}
}