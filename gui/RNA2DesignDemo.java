package gui;

/**
 * Created by tristanwasley on 12/13/17.
 */
public class RNA2DesignDemo {
    public static void main(String[] args) {
        String rnaStr = ">..(((.....)))..(((.....)))..(((.....)))..";
        String rna = ">AAGGCAAAAAGCCAACGCAAAAAGCGAACGGAAAAACCGAA";
        RNA2DesignGUI dialog = new RNA2DesignGUI(rna, rnaStr);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
