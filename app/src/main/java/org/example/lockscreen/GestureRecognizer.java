package org.example.lockscreen;


import android.util.Pair;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.util.ArrayList;
import java.util.Arrays;

import static org.apache.commons.lang3.ArrayUtils.addAll;
import static org.apache.commons.math3.stat.StatUtils.mean;
import static org.apache.commons.math3.stat.StatUtils.sum;
//import static org.apache.commons.math3.stat.StatUtils.min;


public class GestureRecognizer {
    public static void main(String[] args) {

        double[][] t = {{1, 2, 3, 4}, {1, 2, 3, 4}, {1, 2, 3, 4}};
        double[][] r = {{2, 1, 5, 4, 2, 2, 2}, {4, 1, 3, 4, 2, 2, 2}, {6, 8, 9, 4, 2, 2, 2}};

        //[1 1; 1 2 ; 3 4; 4 4; 5 4; 6 7; 8 9]

        //ArrayList<Pair<Integer, Integer>> w = new ArrayList<Pair<Integer, Integer>>();
      /*  ArrayList<Pair<Long, double[]>> w = new ArrayList<Pair<Long, double[]>>();
        w.add(new Pair<Long, double[]>(new Long(1),new double[]{1,0,0}));
        w.add(new Pair<Long, double[]>(new Long(1),new double[]{-7,0,0}));
        w.add(new Pair<Long, double[]>(new Long(1),new double[]{5,0,0}));
        w.add(new Pair<Long, double[]>(new Long(1),new double[]{7,0,0}));
        w.add(new Pair<Long, double[]>(new Long(1),new double[]{2,0,0}));
        w.add(new Pair<Long, double[]>(new Long(1),new double[]{5,0,0}));
        w.add(new Pair<Long, double[]>(new Long(1),new double[]{3,0,0}));
        w.add(new Pair<Long, double[]>(new Long(1),new double[]{-7,0,0}));
        w.add(new Pair<Long, double[]>(new Long(1),new double[]{-5,0,0}));
        double[][] res = prepForComapre(w);*/

//        ArrayList<Pair<Integer, Integer>> res = dtwk(t,r);
//        ArrayList<Pair<Integer, Integer>> w = dtwk(t, r);

      /*  for (int i=0; i <res.length;i++){
            for (int j=0;j < res[i].length;j++){
                System.out.println(res[i][j]);
            }

        }*/
        // ArrayList<Pair<Integer, Integer>> w = new ArrayList<Pair<Integer, Integer>>();
//        w.add(new Pair<Integer, Integer>(1, 2));
//        w.add(new Pair<Integer, Integer>(2, 3));
//        w.add(new Pair<Integer, Integer>(4, 4));
//        w.add(new Pair<Integer, Integer>(6, 5));
//        w.add(new Pair<Integer, Integer>(7, 7));
//        double[][] t = new double[][]{{2, 1, 4}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {2, 1, 4}, {1, 2, 5}, {7, 7, 7}, {0, 0, 0}, {2, 3, 4}, {1, 2, 3}, {1, 2, 3}};
//        double[][] r = new double[][]{{2, 1, 4}, {0, 0, 0}, {2, 1, 4}, {1, 2, 5}, {7, 7, 7}, {0, 0, 0}, {2, 3, 4}, {1, 2, 3}, {1, 2, 3}};
//        double[][] t = new double[][]{{0, 1, 4}, {1, 0, 0}, {4, 0, 0}, {0, 0, 0}, {0, 0, 0}, {25, 1, 4}, {36, 2, 5}, {49, 7, 7}, {64, 0, 0}, {0, 0, 0}, {0, 0, 0}, {121, 2, 3}};
//        double[][][] res = interpolate(new double[][][]{t, t});


       /* for (int i =0; i < res.size();i++){
            System.out.println(res.get(i).getFirst() + " ; " + res.get(i).getSecond());
        }*/
    }


    //    public static ArrayList<Pair<Integer, Integer>> dtwk(double[][] t, double[][] r) {
    public static Pair<ArrayList<Pair<Integer, Integer>>,Double> dtwk(double[][] t, double[][] r) {
        int rows = t.length;
        Array2DRowRealMatrix d = new Array2DRowRealMatrix(t[0].length, r[0].length);
        for (int i = 0; i < rows; i++) {
            double[] tt = t[i];
            double[] rr = r[i];
            double tt_std = new StandardDeviation().evaluate(tt);
            double tt_mean = mean(tt);
            double rr_std = new StandardDeviation().evaluate(rr);
            double rr_mean = mean(rr);
            for (int j = 0; j < tt.length; j++) {
                tt[j] = (tt[j] - tt_mean) / tt_std;
            }
            for (int j = 0; j < rr.length; j++) {
                rr[j] = (rr[j] - rr_mean) / rr_std;
            }


            Array2DRowRealMatrix tt_repmat = repmat(rr.length, tt);
            Array2DRowRealMatrix rr_repamt = repmat(tt.length, rr);
            rr_repamt = (Array2DRowRealMatrix) rr_repamt.transpose();
            Array2DRowRealMatrix subMatrix = tt_repmat.subtract(rr_repamt);
            scalarPower(subMatrix, 2);
            d = d.add(subMatrix);
        }
        System.out.print("");
        Array2DRowRealMatrix D = new Array2DRowRealMatrix(t[0].length, r[0].length);
        D.setEntry(0, 0, d.getEntry(0, 0));
        for (int i = 1; i < t[0].length; i++) {
            D.setEntry(
                    i, 0,
                    d.getEntry(i, 0) + D.getEntry(i - 1, 0));
        }
        for (int j = 1; j < r[0].length; j++) {
            D.setEntry(
                    0, j,
                    d.getEntry(0, j) + D.getEntry(0, j - 1));
        }

        for (int i = 1; i < t[0].length; i++) {
            for (int j = 1; j < r[0].length; j++) {
                D.setEntry(
                        i, j,
                        d.getEntry(i, j) +
                                min(D.getEntry(i - 1, j), D.getEntry(i - 1, j - 1), D.getEntry(i, j - 1)));
            }
        }



        System.out.println("Dist: " + D.getEntry(D.getRowDimension() - 1, D.getColumnDimension() - 1));
        Double dist = D.getEntry(D.getRowDimension() - 1, D.getColumnDimension() - 1);


        int n = t[0].length - 1, m = r[0].length - 1, k = 1;
        ArrayList<Pair<Integer, Integer>> w = new ArrayList<Pair<Integer, Integer>>();
        w.add(new Pair<Integer, Integer>(n, m));
        while ((n + m) != 0) {
            if (n - 1 == -1) {
                m = m - 1;
            } else if (m - 1 == -1) {
                n = n - 1;
            } else {
                double temp_min = min(D.getEntry(n - 1, m), D.getEntry(n - 1, m - 1), D.getEntry(n, m - 1));
                if (temp_min == D.getEntry(n - 1, m)) {
                    n = n - 1;
                } else if (temp_min == D.getEntry(n, m - 1)) {
                    m = m - 1;
                } else {
                    n = n - 1;
                    m = m - 1;
                }
            }
            k = k + 1;
            w.add(new Pair<Integer, Integer>(n, m));
        }

        return new Pair<>(w,dist);


    }

    private static void scalarPower(Array2DRowRealMatrix inMatrix, int power) {
        for (int i = 0; i < inMatrix.getColumnDimension(); i++) {
            for (int j = 0; j < inMatrix.getRowDimension(); j++) {
                inMatrix.setEntry(j, i, Math.pow(inMatrix.getEntry(j, i), power));
            }
        }
    }

    private static Array2DRowRealMatrix repmat(int length, double[] data) {
        Array2DRowRealMatrix repmat = new Array2DRowRealMatrix(new double[data.length][1]);
        repmat.setColumn(0, data);
        ArrayList<Double> ones = new ArrayList<>();
        for (int j = 0; j < length; j++) {
            ones.add((double) 1);
        }
        double[] dArrOnes = ArrayUtils.toPrimitive(ones.toArray(new Double[ones.size()]));
        Array2DRowRealMatrix onesMatrix = new Array2DRowRealMatrix(new double[1][length]);
        onesMatrix.setRow(0, dArrOnes);
        return repmat.multiply(onesMatrix);
    }


    public static double min(double a, double b, double c) {
        return Math.min(Math.min(a, b), c);
    }

    public static double[][] matrixPow(double[][] A, double pow) {
        double[][] ret = new double[A.length][A[0].length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                ret[i][j] = Math.pow(A[i][j], pow);
            }
        }
        return ret;
    }

    public static double[][] matrixSub(double[][] A, double[][] B) {
        double[][] ret = new double[A.length][A[0].length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                ret[i][j] = A[i][j] - B[i][j];
            }
        }
        return ret;
    }

    public static double[][] matrixAdd(double[][] A, double[][] B) {
        double[][] ret = new double[A.length][A[0].length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                ret[i][j] = A[i][j] + B[i][j];
            }
        }
        return ret;
    }

    public static double[][] matrixTranspose(double[][] A) {
        double[][] ret = new double[A[0].length][A.length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                ret[j][i] = A[i][j];
            }
        }
        return ret;
    }

    public static double[] doubleArrayListToArray(ArrayList<Double> inArray) {
        double[] ret = new double[inArray.size()];
        for (int i = 0; i < inArray.size(); i++) {
            ret[i] = inArray.get(i);
        }
        return ret;
    }

    public static double[] rollingAvg(double[] signal, int windowSize) {
        ArrayList<Double> window = new ArrayList<>();
        double[] smoothedSignal = signal.clone();
        for (int i = 0; i < Math.min(windowSize, smoothedSignal.length); i++) {
            window.add(smoothedSignal[i]);
            smoothedSignal[i] = mean(doubleArrayListToArray(window));
        }
        for (int i = windowSize; i < smoothedSignal.length; i++) {
            window.remove(0);
            window.add(smoothedSignal[i]);
            smoothedSignal[i] = mean(doubleArrayListToArray(window));
        }
        return smoothedSignal;
    }


    public static int SMOOTH_WINDOW_SIZE = 7;

    // The new functions

    public static double[][] smoothSensorLog(ArrayList<Pair<Long, double[]>> sensorLog) {
        double[] x = new double[sensorLog.size()];
        double[] y = new double[sensorLog.size()];
        double[] z = new double[sensorLog.size()];

        // load data to vectors
        for (int i = 0; i < sensorLog.size(); i++) {
            double[] record = sensorLog.get(i).second;
            x[i] = record[0];
            y[i] = record[1];
            z[i] = record[2];
        }

        // smooth it
        System.out.println("startSmooth");
        x = rollingAvg(x, SMOOTH_WINDOW_SIZE);
        y = rollingAvg(y, SMOOTH_WINDOW_SIZE);
        z = rollingAvg(z, SMOOTH_WINDOW_SIZE);
        System.out.println("endSmooth");

        double[][] ret = new double[sensorLog.size()][3];
        for (int i = 0; i < sensorLog.size(); i++) {
            ret[i] = new double[]{x[i], y[i], z[i]};
        }
        return ret;
    }


    public static double[][] prepForComapre(ArrayList<Pair<Long, double[]>> sensorLog) {
        double[][] arr = matrixTranspose(smoothSensorLog(sensorLog));
        double[][] abs_arr = new double[3][arr[0].length];
        for (int i = 0; i < 3; i++) {

            double sm_std = new StandardDeviation().evaluate(arr[i]);
            double sm_m = mean(arr[i]);

//            sm_std = Math.pow(Math.abs(sm_std),1/4) * Math.signum(sm_std);
//            sm_m =  Math.pow(Math.abs(sm_m),1/2) * Math.signum(sm_m);
//

            for (int j = 0; j < arr[i].length; j++) {
                arr[i][j] = (arr[i][j] - sm_m) / sm_std;
                abs_arr[i][j] = Math.abs(arr[i][j]);
            }

        }

        double eta = 0;
        int[] ps_arr = new int[3];
        int[] pe_arr = new int[3];
        for (int i = 0; i < 3; i++) {
            eta = sum(abs_arr[i]) / abs_arr[i].length;

            for (int j = 0; j < abs_arr[i].length; j++) {
                if (abs_arr[i][j] > eta) {
                    ps_arr[i] = j;
                    break;
                }

            }
            for (int j = abs_arr[i].length - 1; j >= 0; j--) {
                if (abs_arr[i][j] > eta) {
                    pe_arr[i] = j;
                    break;
                }
            }
        }
        int ps = Math.min(Math.min(ps_arr[0], ps_arr[1]), ps_arr[2]);
        int pe = Math.max(Math.max(pe_arr[0], pe_arr[1]), pe_arr[2]);

        double[][] subArr = new double[3][arr[0].length];
        for (int i = 0; i < 3; i++) {
            subArr[i] = Arrays.copyOfRange(arr[i], ps, pe);
        }
        return subArr;


    }


    public static double compareCleanArrays(double[][] t, double[][] r) {
        Pair<ArrayList<Pair<Integer, Integer>>, Double> dtwkResults = dtwk(t, r);
        ArrayList<Pair<Integer, Integer>> w = dtwkResults.first;
        Double dtwkDist = dtwkResults.second;

//
//        //DEBUG
//        ArrayList<Pair<Integer,Integer>> wfake =new ArrayList<>();
//        Integer[] w1fake = new Integer[]{792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 792, 791, 790, 789, 788, 787, 786, 785, 784, 783, 782, 781, 780, 779, 778, 777, 776, 775, 774, 773, 772, 771, 770, 769, 768, 767, 766, 765, 764, 763, 762, 761, 760, 759, 758, 758, 757, 756, 755, 754, 753, 752, 751, 750, 749, 748, 747, 746, 745, 744, 744, 744, 744, 743, 743, 742, 741, 740, 739, 738, 737, 736, 735, 734, 733, 732, 731, 730, 729, 728, 727, 726, 725, 724, 723, 722, 721, 720, 719, 718, 717, 716, 715, 714, 713, 713, 713, 712, 712, 712, 712, 712, 712, 712, 712, 712, 712, 712, 712, 712, 712, 712, 712, 712, 712, 712, 712, 712, 712, 712, 712, 712, 712, 712, 712, 711, 711, 711, 710, 710, 709, 708, 708, 707, 707, 706, 706, 705, 705, 705, 704, 704, 704, 704, 704, 704, 704, 704, 704, 704, 704, 704, 704, 704, 704, 704, 704, 704, 704, 704, 704, 704, 703, 702, 701, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 700, 699, 699, 698, 698, 698, 698, 698, 697, 697, 696, 696, 695, 694, 693, 692, 691, 690, 689, 688, 687, 686, 685, 684, 683, 682, 681, 680, 679, 678, 677, 676, 675, 674, 673, 672, 671, 670, 669, 668, 667, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 666, 665, 664, 663, 662, 661, 661, 661, 660, 660, 660, 659, 659, 659, 659, 658, 658, 658, 658, 658, 657, 657, 657, 656, 655, 654, 653, 652, 651, 650, 649, 648, 647, 646, 645, 644, 643, 642, 641, 640, 639, 638, 637, 636, 635, 634, 633, 632, 631, 630, 629, 628, 627, 626, 625, 624, 623, 622, 621, 620, 619, 618, 617, 616, 615, 614, 613, 612, 611, 610, 609, 608, 607, 606, 605, 604, 603, 602, 601, 600, 599, 598, 597, 596, 595, 594, 593, 592, 591, 590, 589, 588, 587, 586, 585, 584, 583, 582, 581, 580, 579, 578, 577, 576, 575, 574, 573, 572, 571, 570, 569, 568, 567, 566, 565, 564, 563, 562, 561, 560, 559, 558, 557, 556, 555, 554, 553, 552, 552, 551, 551, 550, 550, 549, 548, 547, 547, 547, 547, 547, 547, 547, 547, 547, 547, 546, 545, 544, 543, 542, 541, 540, 539, 538, 538, 538, 538, 538, 537, 536, 535, 534, 533, 532, 531, 530, 529, 528, 528, 528, 528, 528, 528, 528, 528, 528, 528, 528, 528, 527, 526, 525, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 524, 523, 523, 522, 522, 522, 521, 521, 521, 521, 521, 520, 520, 520, 520, 520, 520, 520, 520, 519, 518, 517, 516, 515, 514, 513, 512, 511, 510, 510, 510, 509, 509, 508, 508, 507, 507, 506, 506, 506, 506, 506, 506, 506, 506, 506, 506, 506, 506, 506, 506, 506, 506, 506, 506, 506, 506, 506, 506, 506, 505, 505, 505, 505, 504, 504, 504, 503, 503, 503, 503, 502, 502, 501, 500, 499, 498, 497, 496, 495, 494, 493, 492, 491, 490, 489, 488, 487, 486, 485, 484, 483, 482, 481, 480, 480, 480, 480, 479, 479, 478, 477, 476, 475, 474, 473, 472, 472, 472, 472, 472, 472, 472, 472, 472, 472, 472, 472, 472, 472, 472, 472, 472, 472, 472, 472, 472, 472, 472, 472, 472, 472, 472, 471, 470, 469, 468, 467, 467, 466, 465, 464, 463, 462, 461, 460, 459, 458, 457, 456, 455, 454, 453, 452, 451, 450, 449, 448, 447, 446, 445, 445, 445, 445, 444, 443, 442, 441, 440, 439, 438, 437, 436, 435, 434, 433, 432, 431, 430, 429, 428, 427, 426, 425, 424, 423, 422, 421, 420, 419, 418, 417, 416, 415, 414, 413, 412, 411, 410, 409, 408, 407, 406, 405, 404, 403, 402, 401, 400, 399, 398, 397, 396, 395, 394, 393, 392, 392, 392, 392, 392, 392, 392, 392, 392, 391, 391, 390, 389, 388, 387, 386, 385, 384, 383, 382, 381, 380, 379, 378, 377, 376, 375, 374, 373, 372, 371, 370, 369, 368, 367, 366, 365, 364, 363, 362, 361, 360, 359, 358, 357, 356, 355, 354, 353, 352, 351, 350, 349, 348, 347, 346, 345, 344, 343, 342, 341, 340, 339, 338, 337, 336, 335, 334, 333, 332, 331, 330, 329, 329, 329, 329, 328, 327, 326, 325, 324, 323, 322, 322, 322, 322, 322, 321, 320, 319, 318, 317, 316, 315, 314, 314, 314, 314, 314, 314, 314, 314, 314, 314, 314, 314, 314, 314, 314, 314, 314, 314, 314, 314, 313, 312, 311, 310, 309, 308, 307, 306, 305, 304, 303, 302, 301, 300, 299, 298, 297, 296, 295, 294, 293, 292, 291, 290, 289, 288, 287, 286, 285, 284, 283, 282, 281, 280, 279, 278, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 277, 276, 275, 274, 273, 272, 271, 270, 269, 268, 267, 266, 265, 264, 263, 262, 261, 260, 259, 258, 257, 256, 255, 254, 253, 252, 251, 250, 249, 248, 247, 246, 245, 244, 243, 242, 241, 240, 239, 238, 237, 236, 235, 234, 233, 232, 231, 230, 229, 228, 227, 226, 225, 224, 223, 222, 221, 220, 219, 218, 217, 216, 215, 214, 213, 212, 211, 210, 209, 208, 207, 206, 205, 204, 203, 202, 201, 200, 199, 198, 197, 196, 195, 194, 193, 192, 191, 190, 189, 188, 187, 186, 185, 184, 183, 182, 181, 180, 179, 178, 178, 178, 178, 178, 178, 178, 178, 178, 178, 177, 176, 175, 174, 173, 172, 171, 170, 169, 168, 167, 166, 165, 164, 163, 162, 161, 160, 159, 158, 157, 156, 155, 154, 153, 152, 151, 150, 149, 148, 147, 146, 145, 144, 143, 142, 141, 140, 139, 138, 137, 136, 135, 134, 133, 132, 131, 130, 129, 128, 127, 126, 125, 124, 123, 122, 121, 120, 119, 118, 117, 116, 115, 114, 113, 112, 111, 110, 109, 108, 107, 106, 105, 104, 103, 102, 101, 100, 99, 98, 97, 96, 95, 94, 93, 92, 91, 90, 89, 88, 87, 86, 85, 84, 83, 82, 81, 80, 79, 78, 77, 76, 75, 74, 73, 72, 72, 71, 71, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 70, 69, 69, 69, 68, 68, 68, 68, 68, 67, 67, 67, 67, 67, 67, 67, 66, 66, 66, 66, 65, 65, 64, 63, 62, 61, 60, 59, 58, 57, 56, 55, 55, 55, 54, 54, 54, 53, 53, 52, 51, 51, 50, 49, 48, 47, 46, 45, 44, 43, 42, 41, 40, 39, 38, 38, 38, 38, 38, 37, 36, 35, 34, 33, 32, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
//        Integer[] w2fake = new Integer[]{766,765,764,763,762,761,760,759,758,757,756,755,754,753,752,751,750,749,748,747,746,745,744,743,742,741,740,739,738,737,736,735,734,733,732,731,730,729,728,727,726,725,724,723,722,721,720,719,718,717,716,715,714,713,712,711,710,709,708,707,706,705,704,703,702,701,700,699,698,697,696,695,694,693,692,691,690,689,688,687,686,685,684,683,682,681,680,679,678,677,676,675,674,673,672,671,670,669,668,667,666,666,666,666,666,666,666,666,666,666,666,666,666,666,666,666,666,666,665,664,663,663,663,663,663,663,663,663,663,663,663,663,663,662,661,660,659,658,657,656,656,656,656,656,656,656,656,656,656,656,656,656,656,656,656,656,656,656,656,656,656,656,656,656,656,656,656,655,654,653,652,651,650,649,648,647,646,645,644,643,642,641,640,639,638,637,636,635,634,633,632,631,630,629,628,627,626,625,624,623,622,621,620,619,618,617,616,615,614,613,612,611,610,609,608,607,606,605,604,603,602,601,600,599,598,597,596,595,594,593,592,591,590,589,588,587,586,585,585,584,583,582,581,580,579,578,577,576,575,574,573,572,571,570,569,568,567,566,565,564,563,562,561,560,559,558,557,556,555,554,553,552,551,550,549,548,547,546,545,544,543,542,541,540,539,538,537,536,535,534,533,532,531,530,529,528,527,526,525,524,523,522,522,521,520,520,520,520,520,520,520,520,520,520,520,520,520,520,520,520,520,520,520,519,519,519,519,519,518,517,516,515,514,513,512,511,510,509,508,507,506,505,504,503,502,501,500,499,498,497,496,495,494,493,492,491,490,489,488,487,486,485,484,483,482,481,480,479,478,477,476,475,474,473,472,471,470,469,468,467,466,465,464,463,462,461,460,459,458,457,456,455,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,454,453,452,451,450,449,448,447,446,445,444,443,442,441,440,439,438,437,436,435,434,433,432,431,430,430,429,429,428,428,427,427,426,425,424,423,422,421,421,420,420,419,418,418,417,417,416,415,414,413,412,411,410,409,408,407,406,405,404,404,403,402,401,400,399,398,397,396,395,394,393,392,391,390,389,388,387,386,385,384,383,382,381,380,379,378,377,376,375,374,373,372,371,370,369,368,367,366,365,364,363,362,361,360,360,360,360,360,360,360,359,358,357,356,355,354,353,352,351,350,349,348,347,346,345,344,343,342,341,340,339,338,337,336,335,334,333,332,331,330,329,328,327,326,325,324,323,322,321,320,319,318,317,316,315,314,313,313,313,313,313,313,313,313,313,313,313,313,313,313,313,313,313,313,313,313,312,311,310,309,308,307,306,305,304,303,302,301,300,299,298,297,296,295,294,293,292,291,290,289,288,287,286,285,284,283,282,281,280,279,278,277,276,275,274,273,272,271,270,269,268,267,266,265,264,264,263,263,263,263,263,263,263,263,263,263,263,262,262,262,261,261,260,259,258,257,256,255,255,254,254,253,252,252,251,251,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,250,249,248,247,246,245,244,243,242,241,240,239,238,237,236,235,234,234,233,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,232,231,231,230,229,229,228,227,226,226,225,225,224,224,223,222,221,220,219,218,218,217,216,216,215,214,213,212,211,210,209,208,207,206,205,204,203,202,201,200,199,198,197,196,195,194,193,192,191,190,189,188,187,186,185,184,183,182,182,181,181,181,180,180,180,179,178,178,178,178,178,178,178,178,178,178,177,176,175,174,174,173,173,172,172,172,171,171,171,170,170,170,170,169,168,167,166,165,164,163,162,161,160,159,158,157,156,155,154,153,152,151,150,149,148,147,146,145,144,143,142,141,140,139,138,137,136,135,134,133,132,131,130,129,128,127,126,125,124,123,122,121,120,119,118,117,116,115,114,113,112,112,111,111,110,110,109,109,109,109,109,109,109,109,109,109,109,109,109,109,109,109,109,109,109,109,109,109,109,109,109,109,108,108,108,108,107,107,107,106,106,106,105,105,105,105,105,105,105,104,104,104,103,103,102,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,101,100,99,98,97,96,96,96,95,94,93,92,91,90,89,88,87,86,85,84,84,84,83,83,82,82,81,80,80,80,80,80,80,80,80,80,80,80,80,80,80,80,80,80,80,80,80,80,80,80,80,80,80,80,80,80,79,79,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,78,77,76,75,74,73,72,71,70,69,68,67,66,65,64,63,62,61,60,59,58,57,56,55,54,53,52,51,50,49,48,47,46,45,44,43,42,41,40,39,38,37,37,37,37,37,37,37,37,36,35,34,33,32,31,30,29,28,27,26,25,24,23,22,21,20,19,19,18,17,16,16,15,14,13,12,11,10,9,8,8,7,6,6,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,4,4,3,3,2,1,1,1};
//        for (int i = 0 ; i < w1fake.length; i++){
//            wfake.add(new Pair<>(w1fake[i],w2fake[i]));
//        }
//        ArrayList<Pair<Integer, Integer>> alfake = w2al(wfake);
//        double[][][] paddedFake = zeroPad(alfake, t, r);
//
//        double[][][] interpolatedFake = interpolate(paddedFake);
//        //DEBUG
//
//

        ArrayList<Pair<Integer, Integer>> al = w2al(w);

        double[][][] padded = zeroPad(al, t, r);

        double[][][] interpolated = interpolate(padded);

        double dist = 0;
        for (int i = 0; i < interpolated[0].length; i++) {
            for (int j = 0; j < 3; j++) {
                dist += Math.abs(interpolated[0][i][j] - interpolated[1][i][j]);
            }
        }
        return dist/3  ;
    }


    public static ArrayList<Pair<Integer, Integer>> w2al(ArrayList<Pair<Integer, Integer>> w) {

        ArrayList<Pair<Integer, Integer>> al = new ArrayList<>();
        for (int i = 1; i < w.size(); i++) {
            if (!w.get(i).first.equals(w.get(i - 1).first) &&
                    !w.get(i).second.equals(w.get(i - 1).second)) {
                al.add(w.get(i));
            }
        }
        return al;
    }

    public static double[][] concat3DArr(double[][] x, double[][] y, double[][] z) {
        double[][] res = new double[x.length + y.length + z.length][3];

        for (int i = 0; i < x.length + y.length + z.length; i++) {
            for (int j = 0; j < 3; j++) {
                if (i < x.length) {
                    res[i][j] = x[i][j];
                } else if (i < x.length + y.length) {
                    res[i][j] = y[i - x.length][j];
                } else {
                    res[i][j] = z[i - x.length - y.length][j];
                }
            }
        }
        return res;
    }


    public static double[][][] zeroPad(ArrayList<Pair<Integer, Integer>> al, double[][] t, double[][] r) {
        double[][] im_t = new double[al.size()][3];
        double[][] im_r = new double[al.size()][3];

        for (int i = 0; i < al.size(); i++) {
            for (int j = 0; j < 3; j++) {
                im_t[i][j] = t[j][al.get(i).first];
                im_r[i][j] = r[j][al.get(i).second];

            }
        }

        int offset = 0;
        int ind_offset = 0;
        for (int i = 0; i < al.size(); i++) {
            int p = al.get(i).first;
            int q = al.get(i).second;

            if (p - q != offset) {
                int d = p - q - offset;
                if (d > 0) {
                    im_t = concat3DArr(Arrays.copyOfRange(im_t, 0, ind_offset + i), new double[d][3], Arrays.copyOfRange(im_t, ind_offset + i, im_t.length));

                    if (i + ind_offset - 1 == -1) {
                        im_r = addAll(new double[d][3], Arrays.copyOfRange(im_r, ind_offset + i, im_r.length));
                    } else {
                        im_r = concat3DArr(Arrays.copyOfRange(im_r, 0, ind_offset + i - 1), new double[d][3], Arrays.copyOfRange(im_r, ind_offset + i - 1, im_r.length));
                    }

                }


                if (d < 0) {
                    im_r = concat3DArr(Arrays.copyOfRange(im_r, 0, ind_offset + i), new double[Math.abs(d)][3], Arrays.copyOfRange(im_r, ind_offset + i, im_r.length));

                    if (i + ind_offset - 1 == -1) {
                        im_t = addAll(new double[Math.abs(d)][3], Arrays.copyOfRange(im_t, ind_offset + i, im_t.length));
                    } else {


                        im_t = concat3DArr(Arrays.copyOfRange(im_t, 0, ind_offset + i - 1), new double[Math.abs(d)][3], Arrays.copyOfRange(im_t, ind_offset + i - 1, im_t.length));
                    }

                }

                offset = p - q;
                ind_offset = ind_offset + Math.abs(d);
            }
        }

        double[][][] res = new double[][][]{im_t, im_r};
        return res;


    }


    public static double[][][] interpolate(double[][][] res) {

        double[][] inter_t;
        double[][] inter_r;


        ArrayList<Double>[] arraylist_x = new ArrayList[]{new ArrayList<>(), new ArrayList<>()};

        ArrayList<Double>[][] arraylist_y = new ArrayList[2][3];

        for (int j = 0; j < 2; j++) {
            arraylist_y[j][0] = new ArrayList<>();
            arraylist_y[j][1] = new ArrayList<>();
            arraylist_y[j][2] = new ArrayList<>();


            for (int i = 0; i < res[j].length; i++) {
                if (!Arrays.equals(res[j][i], new double[]{0, 0, 0}) || i == 0 || i==res[j].length-1) {
                    arraylist_x[j].add((double) i);
                    arraylist_y[j][0].add(res[j][i][0]);
                    arraylist_y[j][1].add(res[j][i][1]);
                    arraylist_y[j][2].add(res[j][i][2]);
                }
            }
        }

        double[] x_t = ArrayUtils.toPrimitive(arraylist_x[0].toArray(new Double[arraylist_x[0].size()]));
        double[] x_r = ArrayUtils.toPrimitive(arraylist_x[0].toArray(new Double[arraylist_x[1].size()]));
        //double[] x_t = ArrayUtils.toPrimitive((Double[]) arraylist_x[0].toArray());
        //double[] x_r = ArrayUtils.toPrimitive((Double[]) arraylist_x[1].toArray());


        double[][] y_t = new double[3][arraylist_x[0].size()];
        double[][] y_r = new double[3][arraylist_x[1].size()];

        for (int i = 0; i < 3; i++) {
            y_t[i] = ArrayUtils.toPrimitive(arraylist_y[0][i].toArray(new Double[arraylist_y[0][i].size()]));
            y_r[i] = ArrayUtils.toPrimitive(arraylist_y[1][i].toArray(new Double[arraylist_y[1][i].size()]));
        }


        PolynomialSplineFunction f[] = new PolynomialSplineFunction[3];
        PolynomialSplineFunction g[] = new PolynomialSplineFunction[3];

        for (int i = 0; i < 3; i++) {
//            f[i] = new SplineInterpolator().
//                    interpolate(x_t, y_t[i]);
//            g[i] = new SplineInterpolator().
//                    interpolate(x_r, y_r[i]);
            f[i] = new org.apache.commons.math3.analysis.interpolation.LinearInterpolator().
                    interpolate(x_t, y_t[i]);
            g[i] = new org.apache.commons.math3.analysis.interpolation.LinearInterpolator().
                    interpolate(x_r, y_r[i]);
        }

        double[][] splined_t = new double[res[0].length][res[0][0].length];
        double[][] splined_r = new double[res[1].length][res[1][1].length];


        for (int i = 0; i < res[0].length; i++) {
            for (int j = 0; j < res[0][0].length; j++) {
                splined_t[i][j] = f[j].value(i);
                splined_r[i][j] = g[j].value(i);
            }
        }

        return new double[][][]{splined_t, splined_r};
    }
}
/*
    private String matchSensorLog(ArrayList<Pair<Long, double[]>> sensorLog) {
        double topMatchDist =  Double.MAX_VALUE;
        String topMatchName = "None found";
        double[][] smoothedLog = AddShape.smoothSensorLog(sensorLog);
        FileInputStream fis ;
        ObjectInputStream is ;
        double[][] readData;
        String compareLog = "";
        for (File shapeDir : new File(this.getFilesDir(), Main.SHAPES_DIR).listFiles()){
            for (File curve: shapeDir.listFiles()){
                try {
                    fis = new FileInputStream(curve);
                    is = new ObjectInputStream(fis);
                    readData = (double[][]) is.readObject();
                    double dist = dtwk.dtwk(readData,smoothedLog);
                    compareLog += shapeDir.getName() + ": " + dist + "\n";
                    if (dist < topMatchDist){
                        topMatchDist = dist;
                        topMatchName  = shapeDir.getName();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        ((TextView)findViewById(R.id.txt_dtwResults)).setText(compareLog);
        return topMatchName;
    }
}*/
