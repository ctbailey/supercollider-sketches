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


b = Buffer.alloc(s, 512, 1); //make a Buffer storage area

b.sine1(1.0/[1,2,3,4,5,6], true, false, true);  //fill the Buffer with wavetable data

b.plot; //stored shape (not in special SuperCollider Wavetable format, for clarity)

{OscN.ar(b,MouseX.kr(10,1000),0,0.1)}.play  //OscN; N means non-interpolating

(
p = thisProcess.nowExecutingPath.dirname +/+ "peace-of-mind-bass.wav";
b = Buffer.read(s, p);

SynthDef("pitchFollow1",{
	var in, amp, freq, hasFreq, out;
	// in = Mix.new(AudioIn.ar([1,2]));
	in = PlayBuf.ar(1, b.bufnum, BufRateScale.kr(b.bufnum), 1, 0, 1);
	amp = Amplitude.kr(in, 0.05, 0.05);
	// amp = 1;
	# freq, hasFreq = Tartini.kr(in, 0.93, 2048, 1500, 2048);
	freq = Lag.kr(freq.cpsmidi.round(1).midicps, 0.05);
	freq = freq * SinOsc.kr(10, 0, 0.01, 1);
	out = Mix.new(VarSaw.ar(freq * [0.5,1,2,4, 8], 0, LFNoise1.kr(0.3,0.1,0.1), amp));
	6.do({
		out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2)
	});
	Out.ar(0,out)
}).play(s);
)