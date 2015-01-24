package uk.co.alynn.games.suchrobot;

public final class Rational implements Comparable<Rational> {
    public final int numerator;
    public final int denominator;

    private Rational(int num, int denom) {
        if (denom == 0) {
            throw new RuntimeException("Division by zero");
        } else if (denom < 0) {
            throw new RuntimeException("Negative denominator");
        }
        numerator = num;
        denominator = denom;
    }

    public int intCast() {
        return numerator / denominator;
    }

    public static int gcd(int a, int b) {
        int tmp;
        while (b != 0) {
            tmp = a;
            a = b;
            b = tmp % b;
        }
        return a;
    }

    public static Rational ratio(int a, int b) {
        int factor = ((a ^ b) < 0) ? -1 : 1;
        if (a < 0)
            a = -a;
        if (b < 0)
            b = -b;
        int cd = gcd(a, b);
        a /= cd;
        b /= cd;
        return new Rational(a*factor, b);
    }
    
    public Rational neg() {
        return new Rational(-numerator, denominator);
    }
    
    public Rational add(Rational rational) {
        if (rational.denominator == denominator) {
            return new Rational(rational.numerator + numerator, denominator);
        }
        int newDenom = denominator * rational.denominator;
        int newNum = numerator*rational.denominator + rational.numerator*denominator;
        return ratio(newNum, newDenom); // TODO: implement me
    }
    
    public Rational sub(Rational rational) {
        return add(rational.neg());
    }

    public Rational recip() {
        int numFactor = numerator < 0 ? -1 : 1;
        int numAbs = numerator < 0 ? -numerator : numerator;
        return new Rational(numFactor * denominator, numAbs);
    }

    public Rational mul(Rational rational) {
        return ratio(numerator*rational.numerator, denominator*rational.denominator);
    }
    
    public Rational div(Rational rational) {
        return mul(rational.recip());
    }

    @Override
    public int compareTo(Rational rational) {
        long lnuma = (long)numerator;
        long ldena = (long)denominator;
        long lnumb = (long)rational.numerator;
        long ldenb = (long)rational.denominator;
        long lfact = lnuma*ldenb;
        long rfact = lnumb*ldena;
        if (lfact < rfact)
            return -1;
        else if (lfact > rfact)
            return 1;
        else
            return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Rational))
            return false;
        Rational robj = (Rational)obj;
        return compareTo(robj) == 0;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(numerator) ^ Integer.hashCode(denominator);
    }

    public String toString() {
        return numerator + "/" + denominator;
    }

    public Rational abs() {
        if (numerator >= 0) {
            return this;
        }
        return new Rational(-numerator, denominator);
    }

    public Rational limitDenominator(int limit) {
        if (limit < 1) {
            throw new RuntimeException("Limit should be at least 1");
        }
        if (denominator < limit) {
            return this;
        }

        int p0 = 0, q0 = 1, p1 = 1, q1 = 0;
        int n = numerator, d = denominator;

        while (true) {
            int a = n / d;
            int q2 = q0 + a*q1;
            if (q2 > limit)
                break;
            int p0_, q0_, p1_, q1_, n_, d_;
            p0_ = p1;
            q0_ = q1;
            p1_ = p0 + a*p1;
            q1_ = q2;
            n_ = d;
            d_ = n - a*d;
            p0 = p0_;
            p1 = p1_;
            q0 = q0_;
            q1 = q1_;
            n = n_;
            d = d_;
        }

        int k = (limit - q0) / q1;
        Rational bound1 = ratio(p0 + k*p1, q0 + k*q1);
        Rational bound2 = ratio(p1, q1);
        Rational error1 = this.sub(bound1).abs();
        Rational error2 = this.sub(bound2).abs();

        if (error1.compareTo(error2) <= 0) {
            return bound2;
        } else {
            return bound1;
        }
    }
}
