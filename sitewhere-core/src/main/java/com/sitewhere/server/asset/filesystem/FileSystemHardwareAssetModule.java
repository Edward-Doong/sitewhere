/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.asset.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sitewhere.configuration.TomcatConfigurationResolver;
import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.rest.model.command.CommandResponse;
import com.sitewhere.server.asset.AssetMatcher;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAssetModule;
import com.sitewhere.spi.command.CommandResult;
import com.sitewhere.spi.command.ICommandResponse;

/**
 * Module that loads a list of hardware assets from an XML file on the filesystem.
 * 
 * @author Derek Adams
 */
public class FileSystemHardwareAssetModule implements IAssetModule<HardwareAsset> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(FileSystemHardwareAssetModule.class);

	/** Module id */
	public static final String MODULE_ID = "fs-hardware";

	/** Module name */
	public static final String MODULE_NAME = "Default Hardware Management";

	/** Filename in SiteWhere config folder that contains hardware assets */
	public static final String HARDWARE_CONFIG_FILENAME = "hardware-assets.xml";

	/** Map of hardware assets by unique id */
	protected Map<String, HardwareAsset> hardwareAssetsById;

	/** Matcher used for searches */
	protected AssetMatcher matcher = new AssetMatcher();

	/** Filename used to load assets */
	private String filename = HARDWARE_CONFIG_FILENAME;

	/** Module id */
	private String moduleId = MODULE_ID;

	/** Module name */
	private String moduleName = MODULE_NAME;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#start()
	 */
	public void start() throws SiteWhereException {
		reload();
	}

	/**
	 * Reloads list of hardware assets from the filesystem.
	 */
	protected void reload() throws SiteWhereException {
		File config = TomcatConfigurationResolver.getSiteWhereConfigFolder();
		File assetsFolder = new File(config, IFileSystemAssetModuleConstants.ASSETS_FOLDER);
		if (!assetsFolder.exists()) {
			throw new SiteWhereException("Assets subfolder not found. Looking for: "
					+ assetsFolder.getAbsolutePath());
		}
		File hardwareConfig = new File(assetsFolder, getFilename());
		if (!hardwareConfig.exists()) {
			throw new SiteWhereException("Assets file missing. Looking for: "
					+ hardwareConfig.getAbsolutePath());
		}
		LOGGER.info("Loading assets from: " + hardwareConfig.getAbsolutePath());

		// Unmarshal assets from XML file and store in data object.
		List<HardwareAsset> assets = MarshalUtils.loadHardwareAssets(hardwareConfig, AssetType.Hardware);
		this.hardwareAssetsById = new HashMap<String, HardwareAsset>();
		for (HardwareAsset asset : assets) {
			hardwareAssetsById.put(asset.getId(), asset);
		}
		showLoadResults();
	}

	/**
	 * Log the number of assets loaded for each type.
	 */
	protected void showLoadResults() {
		String message = "Loaded " + hardwareAssetsById.size() + " hardware assets.";
		LOGGER.info(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#stop()
	 */
	public void stop() throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#getId()
	 */
	public String getId() {
		return getModuleId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#getName()
	 */
	public String getName() {
		return getModuleName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.asset.IAssetModule#isAssetTypeSupported(com.sitewhere.spi.asset
	 * .AssetType)
	 */
	public AssetType getAssetType() {
		return AssetType.Hardware;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.asset.IAssetModule#getAssetById(com.sitewhere.spi.asset.AssetType
	 * , java.lang.String)
	 */
	public HardwareAsset getAssetById(String id) throws SiteWhereException {
		return hardwareAssetsById.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#search(java.lang.String)
	 */
	public List<HardwareAsset> search(String criteria) throws SiteWhereException {
		return search(criteria, hardwareAssetsById);
	}

	/**
	 * Search the given cache for an asset matching the given criteria.
	 * 
	 * @param criteria
	 * @param cache
	 * @return
	 * @throws SiteWhereException
	 */
	protected List<HardwareAsset> search(String criteria, Map<String, HardwareAsset> cache)
			throws SiteWhereException {
		criteria = criteria.toLowerCase();
		List<HardwareAsset> results = new ArrayList<HardwareAsset>();
		if (criteria.length() == 0) {
			results.addAll(cache.values());
		} else {
			for (HardwareAsset asset : cache.values()) {
				if (matcher.isHardwareMatch(asset, criteria)) {
					results.add(asset);
				}
			}
		}
		Collections.sort(results);
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetModule#refresh()
	 */
	public ICommandResponse refresh() throws SiteWhereException {
		try {
			reload();
			showLoadResults();
			return new CommandResponse(CommandResult.Successful, "Refresh successful.");
		} catch (SiteWhereException e) {
			return new CommandResponse(CommandResult.Failed, e.getMessage());
		}
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
}