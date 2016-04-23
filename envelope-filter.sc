
/* A simple envelope filter
 *
 * It tracks the peak amplitude of the input. When the amplitude
 * passes a certain threshold, it triggers an envelope which modulates
 * cutoff frequency of the filter.
 */

(
// Load audio file
p = thisProcess.nowExecutingPath.dirname +/+ "peace-of-mind-bass.wav";
b = Buffer.read(s, p);

SynthDef("bassEnvelopeFilter",{
	// Ugen to actually play the buffer
	var bass = PlayBuf.ar(1, b.bufnum, BufRateScale.kr(b.bufnum), 1, 0, 1);

	// Specify envelope shape
	var env = Env.linen(attackTime: 0.1, sustainTime: 0, releaseTime: 0.1);

	// Track bass's peak amplitude
	var bassAmplitude = Amplitude.kr(bass);
	// Trigger envelope when amplitude is above threshold
	var trig = bassAmplitude >= 0.5;

	var cutoffModulator = EnvGen.kr(env, trig, 500, 400);

	// Modulate frequency of the Moog-emulating filter
	var signal = MoogFF.ar(bass, cutoffModulator, 3.5);

	// Send signal to both outputs
	Out.ar([0,1], signal);
	trig.scope;
}).play;
)