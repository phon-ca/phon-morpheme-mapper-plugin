package ca.phon.session.alignedMorphemes;

import ca.phon.ipa.IPATranscript;
import ca.phon.orthography.*;
import ca.phon.session.*;

/**
 * Represents a set of aligned morphemes across Phon tiers.
 *
 */
public class AlignedMorpheme {

	private final Word alignedWord;

	private final int morphemeIdx;

	AlignedMorpheme(Word word, int morphemeIdx) {
		super();

		this.alignedWord = word;
		this.morphemeIdx = morphemeIdx;
	}

	public Word getAlignedWord() {
		return this.alignedWord;
	}

	public int getMorphemeIdx() {
		return morphemeIdx;
	}

	public OrthoElement getOrthography() {
		MorphemeParser parser = new MorphemeParser();
		OrthoElement orthoElement = getAlignedWord().getOrthography();
		OrthoElement[] morphemes = orthoElement == null ? new OrthoElement[0] : parser.parseOrthography(getAlignedWord().getOrthography());
		return (getMorphemeIdx() >= 0 && getMorphemeIdx() < morphemes.length
				? morphemes[getMorphemeIdx()]
				: null);
	}

	public int getOrthographyWordLocation() {
		MorphemeParser parser = new MorphemeParser();
		OrthoElement orthoElement = getAlignedWord().getOrthography();
		final OrthographyMorphemeVisitor visitor = new OrthographyMorphemeVisitor();
		visitor.visit(orthoElement);

		return (this.morphemeIdx < visitor.getMorphemeIndexes().length
				? visitor.getMorphemeIndexes()[this.morphemeIdx] : -1);
	}

	public IPATranscript getIPATarget() {
		MorphemeParser parser = new MorphemeParser();
		IPATranscript ipaTarget = getAlignedWord().getIPATarget();
		IPATranscript[] morphemes = ipaTarget == null ? new IPATranscript[0] : parser.parseIPA(getAlignedWord().getIPATarget());
		return (getMorphemeIdx() >= 0 && getMorphemeIdx() < morphemes.length
				? morphemes[getMorphemeIdx()]
				: null);
	}

	public IPATranscript getIPAActual() {
		MorphemeParser parser = new MorphemeParser();
		IPATranscript ipaActual = getAlignedWord().getIPAActual();
		IPATranscript[] morphemes = ipaActual == null ? new IPATranscript[0] : parser.parseIPA(getAlignedWord().getIPAActual());
		return (getMorphemeIdx() >= 0 && getMorphemeIdx() < morphemes.length
				? morphemes[getMorphemeIdx()]
				: null);
	}

	public TierString getNotes() {
		MorphemeParser parser = new MorphemeParser();
		TierString note = getAlignedWord().getNotes();
		TierString[] morphemes = note == null ? new TierString[0] : parser.parseTier(getAlignedWord().getNotes());
		return (getMorphemeIdx() >= 0 && getMorphemeIdx() < morphemes.length
				? morphemes[getMorphemeIdx()]
				: null);
	}

	public TierString getUserTier(String tierName) {
		MorphemeParser parser = new MorphemeParser();
		TierString tier = (TierString)getAlignedWord().getTier(tierName);
		TierString[] morphemes = tier == null ? new TierString[0] : parser.parseTier((TierString)getAlignedWord().getTier(tierName));
		return (getMorphemeIdx() >= 0 && getMorphemeIdx() < morphemes.length
				? morphemes[getMorphemeIdx()]
				: null);
	}

	/**
	 * Get text for tierName
	 *
	 * @param tierName the morpheme text for tierName or an empty string (never null)
	 * @return
	 */
	public String getMorphemeText(String tierName) {
		String retVal = "";

		SystemTierType systemTier = SystemTierType.tierFromString(tierName);
		if(systemTier != null) {
			switch (systemTier) {
				case Orthography -> {
					OrthoElement ortho = getOrthography();
					retVal = (ortho != null ? ortho.toString() : "");
				}

				case IPATarget -> {
					IPATranscript ipa = getIPATarget();
					retVal = (ipa != null ? ipa.toString() : "");
				}

				case IPAActual -> {
					IPATranscript ipa = getIPAActual();
					retVal = (ipa != null ? ipa.toString() : "");
				}

				case Notes -> {
					TierString note = getNotes();
					retVal = (note != null ? note.toString() : "");
				}
			}
		} else {
			TierString tierVal = getUserTier(tierName);
			retVal = (tierVal != null ? tierVal.toString() : "");
		}

		return retVal;
	}

}
