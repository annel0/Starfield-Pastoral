package com.stardew.craft.client.sound;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

/**
 * A simple looping UI sound whose pitch can be updated while it plays.
 */
public final class LoopingVariablePitchSoundInstance extends AbstractSoundInstance implements TickableSoundInstance {
	private volatile boolean stopped;

	public LoopingVariablePitchSoundInstance(SoundEvent sound, float volume, float pitch) {
		super(sound, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
		this.looping = true;
		this.delay = 0;
		this.volume = volume;
		this.pitch = pitch;
		this.relative = true;
		this.x = 0.0;
		this.y = 0.0;
		this.z = 0.0;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public void stopNow() {
		this.stopped = true;
	}

	@Override
	public boolean isStopped() {
		return stopped;
	}

	@Override
	public void tick() {
		// No-op; pitch is updated externally. Stop state is polled via isStopped().
	}
}
