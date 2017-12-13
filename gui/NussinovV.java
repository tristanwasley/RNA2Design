package gui;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * Created by tristanwasley on 12/13/17.
 */
public class NussinovV {

    ArrayList<String> _cacheStructs;
    String _cache;

    // To set up
    public void initiate() {
        _cacheStructs = new ArrayList<>();
        _cache = "";
    }


    public ArrayList<String> getStructs(String rnaSeq) {
        initiate();
        String seq = rnaSeq;
        seq = seq.toUpperCase();
        if (!_cache.equals(seq))
        {
            double[][] mfe = fillMatrix(seq);
            _cacheStructs = backtrack(mfe,seq);
            _cache = seq;
        }
        //BigInteger unknown = count(seq);
        return _cacheStructs;
    }


    private boolean canBasePairAll(char a, char b)
    {
        return true;
    }

    private boolean canBasePairBasic(char a, char b)
    {
        if ((a=='G')&&(b=='C'))
            return true;
        if ((a=='C')&&(b=='G'))
            return true;
        if ((a=='U')&&(b=='A'))
            return true;
        if ((a=='A')&&(b=='U'))
            return true;
        if ((a=='G')&&(b=='U'))
            return true;
        if ((a=='U')&&(b=='G'))
            return true;
        return false;
    }

    private double basePairScoreBasic(char a, char b)
    {
        if ((a=='G')&&(b=='C'))
            return 1.0;
        if ((a=='C')&&(b=='G'))
            return 1.0;
        if ((a=='U')&&(b=='A'))
            return 1.0;
        if ((a=='A')&&(b=='U'))
            return 1.0;
        if ((a=='G')&&(b=='U'))
            return 1.0;
        if ((a=='U')&&(b=='G'))
            return 1.0;
        return Double.NEGATIVE_INFINITY;
    }


    private boolean canBasePairNussinov(char a, char b)
    {
        if ((a=='G')&&(b=='C'))
            return true;
        if ((a=='C')&&(b=='G'))
            return true;
        if ((a=='U')&&(b=='A'))
            return true;
        if ((a=='A')&&(b=='U'))
            return true;
        if ((a=='U')&&(b=='G'))
            return true;
        if ((a=='G')&&(b=='U'))
            return true;
        return false;
    }

    private double basePairScoreNussinov(char a, char b)
    {
        if ((a=='G')&&(b=='C'))
            return 3.0;
        if ((a=='C')&&(b=='G'))
            return 3.0;
        if ((a=='U')&&(b=='A'))
            return 2.0;
        if ((a=='A')&&(b=='U'))
            return 2.0;
        if ((a=='U')&&(b=='G'))
            return 1.0;
        if ((a=='G')&&(b=='U'))
            return 1.0;
        return Double.NEGATIVE_INFINITY;
    }

    private boolean canBasePairINRIA(char a, char b)
    {
        if ((a=='U')&&(b=='A'))
            return true;
        if ((a=='A')&&(b=='U'))
            return true;
        if ((a=='G')&&(b=='C'))
            return true;
        if ((a=='C')&&(b=='G'))
            return true;

        if ((a=='A')&&(b=='G'))
            return true;
        if ((a=='G')&&(b=='A'))
            return true;
        if ((a=='U')&&(b=='C'))
            return true;
        if ((a=='C')&&(b=='U'))
            return true;
        if ((a=='A')&&(b=='A'))
            return true;
        if ((a=='U')&&(b=='U'))
            return true;

        if ((a=='U')&&(b=='G'))
            return true;
        if ((a=='G')&&(b=='U'))
            return true;
        if ((a=='A')&&(b=='C'))
            return true;
        if ((a=='C')&&(b=='A'))
            return true;
        return false;
    }

    private double basePairScoreINRIA(char a, char b)
    {
        if ((a=='U')&&(b=='A'))
            return 3;
        if ((a=='A')&&(b=='U'))
            return 3;
        if ((a=='G')&&(b=='C'))
            return 3;
        if ((a=='C')&&(b=='G'))
            return 3;

        if ((a=='A')&&(b=='G'))
            return 2;
        if ((a=='G')&&(b=='A'))
            return 2;
        if ((a=='U')&&(b=='C'))
            return 2;
        if ((a=='C')&&(b=='U'))
            return 2;
        if ((a=='A')&&(b=='A'))
            return 2;
        if ((a=='U')&&(b=='U'))
            return 2;

        if ((a=='U')&&(b=='G'))
            return 1;
        if ((a=='G')&&(b=='U'))
            return 1;
        if ((a=='A')&&(b=='C'))
            return 1;
        if ((a=='C')&&(b=='A'))
            return 1;
        return Double.NEGATIVE_INFINITY;
    }

    private boolean canBasePair(char a, char b)
    {
        //return canBasePairBasic(a,b);
        return canBasePairNussinov(a,b);
        //return canBasePairINRIA(a,b);
    }

    private double basePairScore(char a, char b)
    {
        //return basePairScoreBasic(a,b);
        return basePairScoreNussinov(a,b);
        //return basePairScoreINRIA(a,b);
    }

    public double[][] fillMatrix(String seq)
    {
        int n = seq.length();
        double[][] tab = new double[n][n];
        for(int m=1;m<=n;m++)
        {
            for(int i=0;i<n-m+1;i++)
            {
                int j = i+m-1;
                tab[i][j] = 0;
                if (i<j)
                {
                    tab[i][j] = Math.max(tab[i][j], tab[i+1][j]);
                    for (int k=i+1;k<=j;k++)
                    {
                        if (canBasePair(seq.charAt(i),seq.charAt(k)))
                        {
                            double fact1 = 0;
                            if (k>i+1)
                            {
                                fact1 = tab[i+1][k-1];
                            }
                            double fact2 = 0;
                            if (k<j)
                            {
                                fact2 = tab[k+1][j];
                            }
                            tab[i][j] = Math.max(tab[i][j],basePairScore(seq.charAt(i),seq.charAt(k))+fact1+fact2);
                        }
                    }
                }
            }
        }
        return tab;
    }

    public static ArrayList<Double> combine(double bonus, ArrayList<Double> part1, ArrayList<Double> part2)
    {
        ArrayList<Double> base = new ArrayList<Double>();
        for(double d1: part1)
        {
            for(double d2: part2)
            {
                base.add(bonus+d1+d2);
            }
        }
        return base;
    }

    public static ArrayList<Double> selectBests(ArrayList<Double> base)
    {
        ArrayList<Double> result = new ArrayList<Double>();
        double best = Double.NEGATIVE_INFINITY;
        for(double val: base)
        {
            best = Math.max(val, best);
        }
        for(double val: base)
        {
            if (val == best)
                result.add(val);
        }
        return result;
    }


    private ArrayList<String> backtrack(double[][] tab, String seq)
    {
        return backtrack(tab,seq, 0, seq.length()-1);
    }

    private ArrayList<String> backtrack(double[][] tab, String seq, int i, int j)
    {
        ArrayList<String> resultB = new ArrayList<String>();
        if (i<j)
        {
            ArrayList<Integer> indices = new ArrayList<Integer>();
            indices.add(-1);
            for (int k=i+1;k<=j;k++)
            {
                indices.add(k);
            }
            for (int k : indices)
            {
                if (k==-1)
                {
                    if (tab[i][j] == tab[i+1][j])
                    {
                        for (String s : backtrack(tab, seq, i+1,j))
                        {
                            resultB.add("."+s);
                        }
                    }
                }
                else
                {
                    if (canBasePair(seq.charAt(i),seq.charAt(k)))
                    {
                        double fact1 = 0;
                        if (k>i+1)
                        {
                            fact1 = tab[i+1][k-1];
                        }
                        double fact2 = 0;
                        if (k<j)
                        {
                            fact2 = tab[k+1][j];
                        }
                        if (tab[i][j]==basePairScore(seq.charAt(i),seq.charAt(k))+fact1+fact2)
                        {
                            for (String s1:backtrack(tab, seq, i+1,k-1))
                            {
                                for (String s2:backtrack(tab, seq, k+1,j))
                                {
                                    resultB.add("("+s1+")"+s2);
                                    //this may be the location to add a score keeper for each
                                }
                            }
                        }
                    }
                }
            }
        }
        else if  (i==j)
        {
            resultB.add(".");
        }
        else
        {
            resultB.add("");
        }
        return resultB;
    }

    public BigInteger count(String seq)
    {
        int n = seq.length();

        BigInteger[][] tab = new BigInteger[n][n];
        for(int m=1;m<=n;m++)
        {
            for(int i=0;i<n-m+1;i++)
            {
                int j = i+m-1;
                tab[i][j] = BigInteger.ZERO;
                if (i<j)
                {
                    tab[i][j] = tab[i][j].add(tab[i+1][j]);
                    for (int k=i+1;k<=j;k++)
                    {
                        if (canBasePair(seq.charAt(i),seq.charAt(k)))
                        {
                            BigInteger fact1 = BigInteger.ONE;
                            if (k>i+1)
                            {
                                fact1 = tab[i+1][k-1];
                            }
                            BigInteger fact2 = BigInteger.ONE;
                            if (k<j)
                            {
                                fact2 = tab[k+1][j];
                            }
                            tab[i][j] = tab[i][j].add(fact1.multiply(fact2));
                        }
                    }
                }
                else
                {
                    tab[i][j] = BigInteger.ONE;
                }
            }
        }
        return tab[0][n-1];
    }
}
