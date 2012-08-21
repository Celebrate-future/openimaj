/**
 * Copyright (c) 2011, The University of Southampton and the individual contributors.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *   * 	Redistributions of source code must retain the above copyright notice,
 * 	this list of conditions and the following disclaimer.
 *
 *   *	Redistributions in binary form must reproduce the above copyright notice,
 * 	this list of conditions and the following disclaimer in the documentation
 * 	and/or other materials provided with the distribution.
 *
 *   *	Neither the name of the University of Southampton nor the names of its
 * 	contributors may be used to endorse or promote products derived from this
 * 	software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.openimaj.image.feature.local.engine.asift;

import java.util.Map;

import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.feature.local.list.MemoryLocalFeatureList;
import org.openimaj.image.MBFImage;
import org.openimaj.image.feature.local.affine.AffineParams;
import org.openimaj.image.feature.local.affine.AffineSimulation;
import org.openimaj.image.feature.local.affine.AffineSimulationKeypoint;
import org.openimaj.image.feature.local.affine.ColourASIFT;
import org.openimaj.image.feature.local.engine.DoGSIFTEngineOptions;
import org.openimaj.image.feature.local.engine.Engine;
import org.openimaj.image.feature.local.keypoints.Keypoint;

public class ColourASIFTEngine implements Engine<Keypoint, MBFImage> {
	protected AffineSimulation<LocalFeatureList<Keypoint>, Keypoint, MBFImage, Float[]> asift;
	protected int nTilts = 5;

	public ColourASIFTEngine() {
		this(false);
	}

	public ColourASIFTEngine(boolean hires) {
		asift = new ColourASIFT(hires);
	}

	public ColourASIFTEngine(boolean hires, int nTilts) {
		asift = new ColourASIFT(hires);
		this.nTilts = nTilts;
	}

	public ColourASIFTEngine(DoGSIFTEngineOptions<MBFImage> opts) {
		asift = new ColourASIFT(opts);
	}

	public ColourASIFTEngine(DoGSIFTEngineOptions<MBFImage> opts, int nTilts) {
		asift = new ColourASIFT(opts);
		this.nTilts = nTilts;
	}

	@Override
	public LocalFeatureList<Keypoint> findFeatures(MBFImage image) {
		asift.process(image, nTilts);
		return asift.getKeypoints();
	}

	public LocalFeatureList<Keypoint> findKeypoints(MBFImage image, AffineParams params) {
		return asift.process(image, params);
	}

	public Map<AffineParams, LocalFeatureList<Keypoint>> findKeypointsMapped(MBFImage image) {
		asift.process(image, nTilts);
		return asift.getKeypointsMap();
	}

	public LocalFeatureList<AffineSimulationKeypoint> findSimulationKeypoints(MBFImage image) {
		asift.process(image, nTilts);
		final Map<AffineParams, LocalFeatureList<Keypoint>> keypointMap = asift.getKeypointsMap();
		final LocalFeatureList<AffineSimulationKeypoint> affineSimulationList = new MemoryLocalFeatureList<AffineSimulationKeypoint>();
		for (final AffineParams params : asift.simulationOrder) {
			for (final Keypoint k : keypointMap.get(params)) {
				affineSimulationList.add(new AffineSimulationKeypoint(k, params, asift.simulationOrder.indexOf(params)));
			}
		}
		return affineSimulationList;
	}

}
