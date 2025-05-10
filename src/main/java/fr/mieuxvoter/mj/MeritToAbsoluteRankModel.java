package fr.mieuxvoter.mj;

import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.PI;
import static java.lang.Math.E;

/**
 * This is an experiment.  This is NOT used in computing the ranking of MJ.  Don't worry.  You can ignore this.
 * It is used to approximate the "merit by absolute rank" from the scalar "merit by JM-Score".
 * Both could be used in proportional representation for proportional polls amongst scouts.
 * For proportional polls amongst soldiers (prone to polarized voting), see the Osmotic Favoritism algo instead.
 * What we call "absolute rank" is the rank of a merit profile in the MJ poll with ALL possible merit profiles.
 */
public class MeritToAbsoluteRankModel {

    /**
     * @param merit is expected to be normalized (between 0 and 1)
     * @return the approximation of the absolute rank, normalized
     */
    public double apply(
            double merit,
            int amountOfGrades,
            Integer amountOfJudges
    ) {
        class SigmoidAmplitudeModel {
            final Double coefficient;
            final Double offset;
            final Double origin;
            final Double sin_amplitude;
            final Double sin_origin;
            final Double sin_phase;

            public SigmoidAmplitudeModel(
                    Double coefficient,
                    Double offset,
                    Double origin,
                    Double sin_amplitude,
                    Double sin_origin,
                    Double sin_phase
            ) {
                this.coefficient = coefficient;
                this.offset = offset;
                this.origin = origin;
                this.sin_amplitude = sin_amplitude;
                this.sin_origin = sin_origin;
                this.sin_phase = sin_phase;
            }

            public Double computeAmplitude(Integer amountOfJudges) {
                return
                        this.offset + (this.coefficient / (amountOfJudges - this.origin))
                                +
                                this.sin_amplitude * sin(amountOfJudges * PI + this.sin_phase)
                                        /
                                        (amountOfJudges - this.sin_origin);
            }
        }

        // This bullshit fitting has been made using dirty, dirty python ; but it works well enough for now
        SigmoidAmplitudeModel[] sam;
        if (2 == amountOfGrades) {
            // With 2 grades the merit from MJ-Score is already affine
            return 1.0 - merit;
        } else if (3 == amountOfGrades) {
            // Values derived from rough model fitting ; they can be improved
            sam = new SigmoidAmplitudeModel[]{
                    new SigmoidAmplitudeModel(0.6409350779507367, 0.4965854515219494, -5.9146962453756444, 23.3851437770479187, 0.9996311919466460, 0.0009832013303302),
                    new SigmoidAmplitudeModel(-0.6410295650865494, 0.5034170870490888, -5.9157805848947866, -0.5494767763972728, 1.0001343001977745, 0.0418436294071475),
            };
        } else if (4 == amountOfGrades) {
            // Values derived from rough model fitting ; they can be improved
            sam = new SigmoidAmplitudeModel[]{
                    new SigmoidAmplitudeModel(0.9170475003989843, 0.2456153714826784, -3.5091977159324292, 0.1867944159248675, 0.9990570652741461, -6.1051158548115607),
                    new SigmoidAmplitudeModel(-0.8277524466501042, 0.5019721627432320, -3.0645135231547678, -0.0080383779640542, 1.2071429213468290, 0.5552095403898315),
                    new SigmoidAmplitudeModel(-0.0537159095557622, 0.2509962916400555, -10.3225213727017575, -0.0450613036977610, 0.7945092912788447, 0.7452859647656658),
            };
        } else if (5 == amountOfGrades) {
            // Values derived from rough model fitting ; they can be improved
            sam = new SigmoidAmplitudeModel[]{
                    new SigmoidAmplitudeModel(0.9000482334396634, 0.1206547483774695, -2.4963552848848400, -0.0356967817861015, 1.0359005237315060, -1.5470500509326637),
                    new SigmoidAmplitudeModel(-0.3290841630085418, 0.3771535023430787, -1.2587082942998835, -5.2922128265961055, 0.1750391985549460, -0.0032739374414037),
                    new SigmoidAmplitudeModel(-0.8157881989763880, 0.3768242875184030, -3.6329714453909800, -0.0239089808347504, 0.6837626088956580, 1.5690544889497136),
                    new SigmoidAmplitudeModel(0.1980505155370003, 0.1265655384666737, -2.5951108466266279, -0.1151449718489945, 0.9638237758976738, 0.2475457864562964),
            };
        } else if (6 == amountOfGrades) {
            // Values derived from rough model fitting ; they can be improved
            sam = new SigmoidAmplitudeModel[]{
                    new SigmoidAmplitudeModel(0.7708075223467123, 0.0580869399899168, -1.7708756450606116, -0.0514019515740431, 1.0922318721535316, 5.5435707901018292),
                    new SigmoidAmplitudeModel(0.0113468236267469, 0.2593847095025533, 3.4080676150197013, 0.3127399704834197, 4.5162752552045529, 0.0246261384044150),
                    new SigmoidAmplitudeModel(-0.9580137264950088, 0.3756958174463476, -3.6912376115661321, -0.0808529635154282, 1.1932023599818111, 6.3582517865739003),
                    new SigmoidAmplitudeModel(-0.3759791146003723, 0.2517848780681173, -2.7201748200294440, -0.0411965223777122, 0.4881179927844195, -5.3011065325748721),
                    new SigmoidAmplitudeModel(0.2852468211981568, 0.0634098062656290, -2.6247252814193711, 0.0250299350511801, 1.0159131152232956, -1.5837886294153409),
            };
        } else if (7 == amountOfGrades) {
            // Values derived from rough model fitting ; they can be improved
            sam = new SigmoidAmplitudeModel[]{
                    new SigmoidAmplitudeModel(0.5151336373041772, 0.0304017096437998, -0.1560819745436698, -0.0642768687910415, 3.7019618565115722, -0.2267673450950530),
                    new SigmoidAmplitudeModel(0.8321495032592745, 0.1538010001096599, -10.1403742732170450, 0.1452337649130754, 2.9093303593527824, 0.1670760936959231),
                    new SigmoidAmplitudeModel(-0.5832534017217945, 0.3128738036537556, -2.4481699553712186, 1.7698591489043021, 0.0064898411429031, -3.1491904326892173),
                    new SigmoidAmplitudeModel(-0.9135479603269890, 0.3121169039235479, -4.0419384013683608, -0.0398619334678863, 2.2608983418537969, -3.5661704309341040),
                    new SigmoidAmplitudeModel(-0.0358891062680384, 0.1592742142625385, 0.8473094470570051, -0.1720450496934443, 0.8776512589952787, 0.1900715592340584),
                    new SigmoidAmplitudeModel(0.2965479931458628, 0.0309932939590777, -2.7064785369970221, -0.0616634512919992, 3.3069369590264279, -3.3295936102008192),
            };
        } else {
            // Let's add support for more grades later
            return 1.0;
        }

        Double sumOfAmplitudes = 0.0;
        Double[] amplitudes = new Double[amountOfGrades];
        for (int i = 0; i < amountOfGrades - 1; i++) {
            amplitudes[i] = sam[i].computeAmplitude(amountOfJudges);
            sumOfAmplitudes += amplitudes[i];
        }
        for (int i = 0; i < amountOfGrades - 1; i++) {
            amplitudes[i] = amplitudes[i] / sumOfAmplitudes;
        }

        double tightness = 96.0; // derived from fitting
        double rank = 0.0;  // from 0.0 (exclusive) to 1.0 (inclusive) ; is 'double' enough precision?
        for (int i = 0; i < amountOfGrades - 1; i++) {
            rank += amplitudes[i] * sigmoid(
                    merit,
                    tightness,
                    (2.0 * i + 1.0) / (2.0 * (amountOfGrades - 1))
            );
        }

        return rank;
    }

    private double sigmoid(double x, double tightness, double origin) {
        return 1.0 / (1.0 + pow(E, tightness * (x - origin)));
    }
}
