package infovk.random_bots.bunto_bot;

import static java.lang.Math.PI;

/**
 * Utility class for dealing with angles.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Tobias Zimmermann (adaption)
 */
class Utils {
    static final double NEAR_DELTA = .00001;
    private final static double PI_OVER_TWO = PI / 2;
    private final static double THREE_PI_OVER_TWO = 3 * PI / 2;
    private final static double TWO_PI = 2 * PI;

    // Hide the default constructor as this class only provides static method
    private Utils() {
    }

    /**
     * Normalizes an angle to an absolute angle.
     * The normalized angle will be in the range from 0 to 360, where 360
     * itself is not included.
     *
     * @param angle the angle to normalize
     * @return the normalized angle that will be in the range of [0,360[
     */
    static double normalAbsoluteAngle(double angle) {
        return (angle %= 360) >= 0 ? angle : (angle + 360);
    }

    /**
     * Normalizes an angle to a relative angle.
     * The normalized angle will be in the range from -180 to 180, where 180
     * itself is not included.
     *
     * @param angle the angle to normalize
     * @return the normalized angle that will be in the range of [-180,180[
     */
    static double normalRelativeAngle(double angle) {
        return (angle %= 360) >= 0 ? (angle < 180) ? angle : angle - 360 : (angle >= -180) ? angle : angle + 360;
    }

    /**
     * Normalizes an angle to be near an absolute angle.
     * The normalized angle will be in the range from 0 to 360, where 360
     * itself is not included.
     * If the normalized angle is near to 0, 90, 180, 270 or 360, that
     * angle will be returned. The {@link #isNear(double, double) isNear}
     * method is used for defining when the angle is near one of angles listed
     * above.
     *
     * @param angle the angle to normalize
     * @return the normalized angle that will be in the range of [0,360[
     * @see #normalAbsoluteAngle(double)
     * @see #isNear(double, double)
     */
    static double normalNearAbsoluteAngle(double angle) {
        angle = (angle %= 360) >= 0 ? angle : (angle + 360);

        if (isNear(angle, 180)) {
            return 180;
        } else if (angle < 180) {
            if (isNear(angle, 0)) {
                return 0;
            } else if (isNear(angle, 90)) {
                return 90;
            }
        } else {
            if (isNear(angle, 270)) {
                return 270;
            } else if (isNear(angle, 360)) {
                return 0;
            }
        }
        return angle;
    }

    /**
     * Tests if the two {@code double} values are near to each other.
     * It is recommended to use this method instead of testing if the two
     * doubles are equal using an this expression: {@code value1 == value2}.
     * The reason being, that this expression might never become
     * {@code true} due to the precision of double values.
     * Whether or not the specified doubles are near to each other is defined by
     * the following expression:
     * {@code (Math.abs(value1 - value2) < .00001)}
     *
     * @param value1 the first double value
     * @param value2 the second double value
     * @return {@code true} if the two doubles are near to each other;
     * {@code false} otherwise.
     */
    static boolean isNear(double value1, double value2) {
        return (Math.abs(value1 - value2) < NEAR_DELTA);
    }

    /**
     * Returns the trigonometric sine of an angle in degrees.  Special cases:
     * <ul><li>If the argument is NaN or an infinity, then the
     * result is NaN.
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.</ul>
     *
     * <p>The computed result must be within 1 ulp of the exact result.
     * Results must be semi-monotonic.
     *
     * @param val an angle, in degrees.
     * @return the sine of the argument.
     */
    static double sin(double val) {
        return Math.sin(Math.toRadians(val));
    }

    /**
     * Returns the trigonometric cosine of an angle in degrees. Special cases:
     * <ul><li>If the argument is NaN or an infinity, then the
     * result is NaN.</ul>
     *
     * <p>The computed result must be within 1 ulp of the exact result.
     * Results must be semi-monotonic.
     *
     * @param val an angle, in degrees.
     * @return the cosine of the argument.
     */
    static double cos(double val) {
        return Math.cos(Math.toRadians(val));
    }

    /**
     * Returns the trigonometric tangent of an angle in degrees.  Special cases:
     * <ul><li>If the argument is NaN or an infinity, then the result
     * is NaN.
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.</ul>
     *
     * <p>The computed result must be within 1 ulp of the exact result.
     * Results must be semi-monotonic.
     *
     * @param val an angle, in degrees.
     * @return the tangent of the argument.
     */
    static double tan(double val) {
        return Math.tan(Math.toRadians(val));
    }

    /**
     * Returns the arc sine of a value; the returned angle is in the
     * range -90 through 90.  Special cases:
     * <ul><li>If the argument is NaN or its absolute value is greater
     * than 1, then the result is NaN.
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.</ul>
     *
     * <p>The computed result must be within 1 ulp of the exact result.
     * Results must be semi-monotonic.
     *
     * @param val the value whose arc sine is to be returned.
     * @return the arc sine of the argument.
     */
    static double asin(double val) {
        return Math.toDegrees(Math.asin(val));
    }

    /**
     * Returns the arc cosine of a value; the returned angle is in the
     * range 0 through 180.  Special case:
     * <ul><li>If the argument is NaN or its absolute value is greater
     * than 1, then the result is NaN.</ul>
     *
     * <p>The computed result must be within 1 ulp of the exact result.
     * Results must be semi-monotonic.
     *
     * @param val the value whose arc cosine is to be returned.
     * @return the arc cosine of the argument.
     */
    static double acos(double val) {
        return Math.toDegrees(Math.acos(val));
    }

    /**
     * Returns the arc tangent of a value; the returned angle is in the
     * range -90 through 90.  Special cases:
     * <ul><li>If the argument is NaN, then the result is NaN.
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.</ul>
     *
     * <p>The computed result must be within 1 ulp of the exact result.
     * Results must be semi-monotonic.
     *
     * @param val the value whose arc tangent is to be returned.
     * @return the arc tangent of the argument.
     */
    static double atan(double val) {
        return Math.toDegrees(Math.atan(val));
    }

    /**
     * Returns the angle <i>theta</i> from the conversion of rectangular
     * coordinates ({@code x},&nbsp;{@code y}) to polar
     * coordinates (r,&nbsp;<i>theta</i>).
     * This method computes the phase <i>theta</i> by computing an arc tangent
     * of {@code y/x} in the range of -180 to 180. Special
     * cases:
     * <ul><li>If either argument is NaN, then the result is NaN.
     * <li>If the first argument is positive zero and the second argument
     * is positive, or the first argument is positive and finite and the
     * second argument is positive infinity, then the result is positive
     * zero.
     * <li>If the first argument is negative zero and the second argument
     * is positive, or the first argument is negative and finite and the
     * second argument is positive infinity, then the result is negative zero.
     * <li>If the first argument is positive zero and the second argument
     * is negative, or the first argument is positive and finite and the
     * second argument is negative infinity, then the result is 180.
     * <li>If the first argument is negative zero and the second argument
     * is negative, or the first argument is negative and finite and the
     * second argument is negative infinity, then the result is -180.
     * <li>If the first argument is positive and the second argument is
     * positive zero or negative zero, or the first argument is positive
     * infinity and the second argument is finite, then the result is 90.
     * <li>If the first argument is negative and the second argument is
     * positive zero or negative zero, or the first argument is negative
     * infinity and the second argument is finite, then the result is -90.
     * <li>If both arguments are positive infinity, then the result is 45.
     * <li>If the first argument is positive infinity and the second argument
     * is negative infinity, then the result is 135.
     * <li>If the first argument is negative infinity and the second argument
     * is positive infinity, then the result is -45.
     * <li>If both arguments are negative infinity, then the result is -135.
     * </ul>
     * <p>The computed result must be within 2 ulps of the exact result.
     * Results must be semi-monotonic.
     *
     * @param y the ordinate coordinate
     * @param x the abscissa coordinate
     * @return the <i>theta</i> component of the point
     * (<i>r</i>,&nbsp;<i>theta</i>)
     * in polar coordinates that corresponds to the point
     * (<i>x</i>,&nbsp;<i>y</i>) in Cartesian coordinates.
     */
    static double atan2(double y, double x) {
        return Math.toDegrees(Math.atan2(y, x));
    }
}
