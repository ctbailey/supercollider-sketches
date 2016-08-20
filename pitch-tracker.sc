/* A simple pitch tracker.
 *
 * It modulates a synth's frequency and amplitude to mimic
 * that of the input signal.
 */
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