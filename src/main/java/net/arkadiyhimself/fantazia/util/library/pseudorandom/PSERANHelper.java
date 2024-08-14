package net.arkadiyhimself.fantazia.util.library.pseudorandom;

/**
 * The probability of an effect to occur (or proc) on the N-th test
 * since the last successful proc is given by P(N) = C * N.
 * For each instance which could trigger the effect but does not,
 * the PRD augments the probability of the effect happening for
 * the next instance by a constant C.
 * This constant, which is also the initial probability, is lower
 * than the listed probability of the effect it is shadowing.
 * Once the effect occurs, the counter is reset.
 * <br>
 * <br>
 * More details: <a href="https://gaming.stackexchange.com/questions/161430/calculating-the-constant-c-in-dota-2-pseudo-random-distribution">...</a> - how the value of C is calculated
 */
public class PSERANHelper {
    /**
     * Calculates the value of constant C depending on the supposed chance of effect (the chance which is shown to user)
     * @param P the supposed chance of effect to occur, 0 < P < 1;
     * @return the value of C depending on the supposed change
     */
    public static double calculateC(double P) {
        double cUpper = P;
        double cLower = 0;
        double cMid;
        double p1;
        double p2 = 1;

        while(true) {
            cMid = (cUpper + cLower) / 2;
            p1 = calculateP(cMid);
            if (Math.abs(p1 - p2)<= 0.001) break;
            if (p1 > P) cUpper = cMid;
            else cLower = cMid;
            p2 = p1;
        }
        return cMid;
    }

    private static double calculateP(double C) {
        double pProcOnN;
        double pProcByN = 0;
        double sumNpProcOnN = 0;

        int maxFails = (int)Math.ceil(1 / C);
        for (int N = 1; N <= maxFails; ++N) {
            pProcOnN = Math.min(1, N * C) * (1 - pProcByN);
            pProcByN += pProcOnN;
            sumNpProcOnN += N * pProcOnN;
        }
        return (1 / sumNpProcOnN);
    }
}
