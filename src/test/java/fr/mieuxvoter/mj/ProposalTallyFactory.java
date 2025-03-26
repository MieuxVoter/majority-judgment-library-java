package fr.mieuxvoter.mj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ProposalTallyFactory {

    private final Integer amountOfGrades;
    private final Integer amountOfJudges;

    public ProposalTallyFactory(
            Integer amountOfGrades,
            Integer amountOfJudges
    ) {
        this.amountOfGrades = amountOfGrades;
        this.amountOfJudges = amountOfJudges;
    }

    /**
     * Be careful, this method explodes quite fast.
     *
     * @return all the possible proposal tallies for amountOfGrades and amountOfJudges.
     */
    public ProposalTallyInterface[] generateAll() {
        ArrayList<ProposalTally> all = new ArrayList<>();

        List<Integer[]> tallies = multichoose(amountOfGrades, amountOfJudges);
        for (Integer[] t : tallies) {
            all.add(new ProposalTally(t));
        }

        return all.toArray(new ProposalTally[0]);
    }

    /**
     * All the ways to distribute k balls into n boxes.
     * See <a href="https://en.wikipedia.org/wiki/Multiset">Multiset on Wikipedia</a>.
     *
     * @param n amount of boxes
     * @param k amount of balls
     */
    private List<Integer[]> multichoose(Integer n, Integer k) {
        assert n >= 0;
        assert k >= 0;

        ArrayList<Integer[]> out = new ArrayList<>();

        if (k == 0) {
            ArrayList<Integer> set = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                set.add(0);
            }
            out.add(set.toArray(new Integer[0]));
            return out;
        }

        if (n == 0) {
            return out;
        }

        if (n == 1) {
            ArrayList<Integer> set = new ArrayList<>();
            set.add(k);
            out.add(set.toArray(new Integer[0]));
            return out;
        }

        for (Integer[] set: multichoose(n-1, k)) {
            ArrayList<Integer> seth = new ArrayList<>();
            seth.add(0);
            seth.addAll(Arrays.asList(set));
            out.add(seth.toArray(new Integer[0]));
        }

        for (Integer[] set: multichoose(n, k-1)) {
            ArrayList<Integer> seth = new ArrayList<>();

            seth.add(set[0] + 1);
            seth.addAll(Arrays.asList(Arrays.copyOfRange(set, 1, set.length)));
            out.add(seth.toArray(new Integer[0]));
        }

        return out;
    }
}