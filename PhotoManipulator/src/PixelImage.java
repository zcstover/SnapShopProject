import java.awt.image.*;

/**
 * Provides an interface to a picture as an array of Pixels
 */
public class PixelImage
{
  private BufferedImage myImage;
  private int width;
  private int height;

  /**
   * Map this PixelImage to a real image
   * @param bi The image
   */
  public PixelImage(BufferedImage bi)
  {
    // initialise instance variables
    this.myImage = bi;
    this.width = bi.getWidth();
    this.height = bi.getHeight();
  }

  /**
   * Return the width of the image
   */
  public int getWidth()
  {
    return this.width;
  }

  /**
   * Return the height of the image
   */
  public int getHeight()
  {
    return this.height;
  }

  /**
   * Return the BufferedImage of this PixelImage
   */
  public BufferedImage getImage()
  {
    return this.myImage;
  }

  /**
   * Return the image's pixel data as an array of Pixels.  The
   * first coordinate is the x-coordinate, so the size of the
   * array is [width][height], where width and height are the
   * dimensions of the array
   * @return The array of pixels
   */
  public Pixel[][] getData()
  {
    Raster r = this.myImage.getRaster();
    Pixel[][] data = new Pixel[r.getHeight()][r.getWidth()];
    int[] samples = new int[3];

    for (int row = 0; row < r.getHeight(); row++)
    {
      for (int col = 0; col < r.getWidth(); col++)
      {
        samples = r.getPixel(col, row, samples);
        Pixel newPixel = new Pixel(samples[0], samples[1], samples[2]);
        data[row][col] = newPixel;
      }
    }

    return data;
  }

  /**
   * Set the image's pixel data from an array.  This array matches
   * that returned by getData().  It is an error to pass in an
   * array that does not match the image's dimensions or that
   * has pixels with invalid values (not 0-255)
   * @param data The array to pull from
   */
  public void setData(Pixel[][] data)
  {
    int[] pixelValues = new int[3];     // a temporary array to hold r,g,b values
    WritableRaster wr = this.myImage.getRaster();

    if (data.length != wr.getHeight())
    {
      throw new IllegalArgumentException("Array size does not match");
    }
    else if (data[0].length != wr.getWidth())
    {
      throw new IllegalArgumentException("Array size does not match");
    }

    for (int row = 0; row < wr.getHeight(); row++)
    {
      for (int col = 0; col < wr.getWidth(); col++)
      {
        pixelValues[0] = data[row][col].red;
        pixelValues[1] = data[row][col].green;
        pixelValues[2] = data[row][col].blue;
        wr.setPixel(col, row, pixelValues);
      }
    }
  }
  
  /*
   * process each pixel in a pixelimage based on a 3x3 around it, 
   * using a weighted array to weight it with various filters
   */
  
  public Pixel[][] WeightedPixels(int[][] weights, PixelImage PI, int scale)
  {
	  Pixel[][] StartPixel = PI.getData();
	  Pixel[][] returnPixels = PI.getData();
	  for(int row = 1; row < PI.getHeight() - 1; row++) 
	  {
		  for(int col = 1; col < PI.getWidth() - 1; col++)
		  {
			  int Red = 0;
			  int Blue = 0;
			  int Green = 0;
			  for(int AvgRow = -1; AvgRow <= 1; AvgRow ++)
			  {
				  for(int AvgCol = -1; AvgCol <= 1; AvgCol ++)
				  {
					  //System.out.println(row + " " + col + " " + PI.getHeight() + " "+ PI.getWidth());
					  Red += StartPixel[row + AvgRow][col + AvgCol].red 
							  * weights[AvgRow + 1][AvgCol + 1];

					  Green += StartPixel[row + AvgRow][col + AvgCol].green 
							  * weights[AvgRow + 1][AvgCol + 1];

					  Blue += StartPixel[row + AvgRow][col + AvgCol].blue 
							  * weights[AvgRow + 1][AvgCol + 1];
					  
				  }
			  }
			  Red = Red/scale;
			  Green = Green/scale;
			  Blue = Blue/scale;
			  Pixel pxl = new Pixel(Red,Green,Blue);
			  returnPixels[row][col] = pxl;
			  
		  }
	  }
	  return returnPixels;
  }

  // add a method to compute a new image given weighted averages
}