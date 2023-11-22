package com.elliptic.curve;

import org.bouncycastle.math.ec.ECPoint;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Objects;

@SpringBootApplication
public class CurveApplication {

	private static final BigInteger P = new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853951");
	private static final BigInteger A = new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853948");
	private static final BigInteger B = new BigInteger("41058363725152142129326129780047268409114441015993725554835256314039467401291");
	private static final BigInteger GX = new BigInteger("48439561293906451759052585252797914202762949526041747995844080717082404635286");
	private static final BigInteger GY = new BigInteger("36134250956749795798585127919587881956611106672985015071877198253568414405109");
	private static final BigInteger N = new BigInteger("115792089210356248762697446949407573529996955224135760342422259061068512044369");

	private static final org.bouncycastle.math.ec.ECCurve.Fp curve = new org.bouncycastle.math.ec.ECCurve.Fp(P, A, B, N,BigInteger.ONE);
	private static final ECPoint G = curve.createPoint(GX, GY);

	private static ECPoint basePointGGet() {
		return G;
	}

	private static ECPoint ECPointGen(BigInteger x, BigInteger y) {
		return curve.createPoint(x, y);
	}

	private static boolean isOnCurveCheck(ECPoint a) {
		a = a.normalize();
		return curve.isValidFieldElement(a.getAffineXCoord().toBigInteger()) &&
				curve.isValidFieldElement(a.getAffineYCoord().toBigInteger());
	}

	private static ECPoint addECPoints(ECPoint a, ECPoint b) {
		return a.add(b);
	}

	private static ECPoint doubleECPoints(ECPoint a) {
		return a.twice();
	}

	private static ECPoint scalarMult(BigInteger k, ECPoint a) {
		return a.multiply(k);
	}

	private static String ECPointToString(ECPoint point) {
		return point.getAffineXCoord().toBigInteger().toString(16) + "," + point.getAffineYCoord().toBigInteger().toString(16);
	}

	private static ECPoint stringToECPoint(String s) {
		String[] coordinates = s.split(",");
		if (coordinates.length != 2) {
			return null;
		}
		BigInteger x = new BigInteger(coordinates[0], 16);
		BigInteger y = new BigInteger(coordinates[1], 16);
		return curve.createPoint(x, y);
	}

	private static void printECPoint(ECPoint point) {
		System.out.println("X: " + point.getAffineXCoord().toBigInteger().toString(16));
		System.out.println("Y: " + point.getAffineYCoord().toBigInteger().toString(16));
		System.out.println();
	}
	private static void tests() {
		ECPoint basePoint = basePointGGet();
		System.out.println("\nBase Point:");
		printECPoint(basePoint);

		BigInteger x1 = new BigInteger("1234567890123456789012345678901234567890123456789012345678901234");
		BigInteger y1 = new BigInteger("9876543210987654321098765432109876543210987654321098765432109876");
		ECPoint newPoint1 = ECPointGen(x1, y1);
		BigInteger x2 = new BigInteger("99900999");
		BigInteger y2 = new BigInteger("9851151978220366039615215600016114916758994648000171558822843077797956858945");
		ECPoint newPoint2 = ECPointGen(x2, y2);
		System.out.println("Generated Point1:");
		printECPoint(newPoint1);
		System.out.println("Generated Point2:");
		printECPoint(newPoint2);

		System.out.println("isOnCurve: " + isOnCurveCheck(newPoint1));
		System.out.println("isOnCurve: " + isOnCurveCheck(newPoint2) + "\n");

		ECPoint addition = addECPoints(newPoint2, newPoint2).normalize();
		System.out.println("Addition of the points:");
		printECPoint(addition);

		ECPoint doublePoint = doubleECPoints(newPoint2).normalize();
		System.out.println("Double of the point:");
		printECPoint(doublePoint);

		BigInteger scalar = BigInteger.valueOf(2);
		ECPoint scalarMult = scalarMult(scalar, newPoint2).normalize();
		System.out.println("Multiply by scalar:");
		printECPoint(scalarMult);

		System.out.println("Serialize: ");
		System.out.println(ECPointToString(newPoint1));

		System.out.println("Deserialize: ");
		String serialized = "30046030f26f462d7ac21a27eb9d53fff233c7acd12d87e96aff2,1802301c24dc7603f86d1d445f746905d09b7af3b84aea59bdbb34";
		printECPoint(Objects.requireNonNull(stringToECPoint(serialized)));
	}

	public static void main(String[] args) {
		ECPoint G = basePointGGet();
		BigInteger k = new BigInteger(256, new SecureRandom()).setBit(256);
		BigInteger d = new BigInteger(256, new SecureRandom()).setBit(256);
		ECPoint H1 = scalarMult(d, G).normalize();
		ECPoint H2 = scalarMult(k, H1).normalize();
		ECPoint H3 = scalarMult(k, G).normalize();
		ECPoint H4 = scalarMult(d, H3).normalize();
		tests();

		boolean result = H2.getAffineXCoord().toBigInteger().equals(H4.getAffineXCoord().toBigInteger()) &&
				H2.getAffineYCoord().toBigInteger().equals(H4.getAffineYCoord().toBigInteger());
		System.out.println("Equation Result: " + result);
	}
}




