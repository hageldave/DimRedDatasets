package hageldave.dimred.datasets;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class DimensionsTest {

	@Test
	void iris() {
		double[][] data = IrisDataset.getInstance().data;
		assertEquals(150, data.length);
		assertEquals(4, data[0].length);
	}
	
	@Test
	void mnist() {
		double[][] data = MNISTDataset.getInstance().data;
		assertEquals(60_000, data.length);
		assertEquals(28*28, data[0].length);
	}
	
}
