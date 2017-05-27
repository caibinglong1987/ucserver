package com.roamtech.uc.zxing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.roamtech.uc.handler.SNGenerator;
import com.roamtech.uc.model.UserEVoucher;

public class EVoucherGenerator {
	private static String ROOT_DIR="/opt/";
	public static void deleteEVImage(String imagefile) {
		File file = new File(ROOT_DIR+imagefile);
		if(file.isFile() && file.exists()) {
			file.delete();
		}
	}
	public static String generatorMemberEV(UserEVoucher uev, String content, String background, String outfile) {
		File fileOne = new File(ROOT_DIR+background);
		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();

		BitMatrix matrix;
		try {
			Image src = ImageIO.read(fileOne);
			int wideth=1080;//src.getWidth(null);
			int height=680;//src.getHeight(null);

			BufferedImage bgimage=new BufferedImage(wideth,height,BufferedImage.TYPE_INT_ARGB);//BufferedImage.TYPE_USHORT_555_RGB);
			Graphics2D g=bgimage.createGraphics();
			g.drawImage(src,0,0,wideth,height,null);
			g.setColor(Color.WHITE);

			int barcodeWidth = 840;//30*content.length();
			int barcodeHeight = 140;
			matrix = new MultiFormatWriter().encode(
					content, BarcodeFormat.CODE_128, barcodeWidth,
					barcodeHeight, hints);
			BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix, new MatrixToImageConfig(MatrixToImageConfig.BLACK,MatrixToImageConfig.WHITE));//0x80FFFFFF));
			g.fillRect(100,293,880,150);
			g.drawImage(image, (wideth-barcodeWidth)/2, 298, barcodeWidth, barcodeHeight, null);
			g.setColor(Color.WHITE);
			g.setFont(new Font("Calibri",Font.PLAIN,56));
			StringBuilder strBuilder = new StringBuilder();
			for(int i=0;i<content.length();i++) {
				strBuilder = strBuilder.append(content.charAt(i)).append("    ");
			}

			g.drawString(strBuilder.toString(), (wideth-barcodeWidth)/2+50, 490);
			g.dispose();
			Path opath = Paths.get(ROOT_DIR+outfile);
			File odir = opath.getParent().toFile();
			if(!odir.exists()) {
				odir.mkdirs();
			}
			if (!ImageIO.write(bgimage, "PNG", opath.toFile())) {
				System.out.println("ImageIO.write failed");
				return null;
			}
			return outfile;
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static String generatorEV(UserEVoucher uev, String content, String background, Integer quantity, String outfile) {
		System.out.println(content+" "+background+" "+outfile);
		File fileOne = new File(ROOT_DIR+background);
		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
	    /*if (config.errorCorrectionLevel != null) {
	      hints.put(EncodeHintType.ERROR_CORRECTION, config.errorCorrectionLevel);
	    }*/
		BitMatrix matrix;
		try {
			Image src = ImageIO.read(fileOne); 
		    int wideth=1080;//src.getWidth(null); 
		    int height=680;//src.getHeight(null); 
			
		    BufferedImage bgimage=new BufferedImage(wideth,height,BufferedImage.TYPE_INT_ARGB);//BufferedImage.TYPE_USHORT_555_RGB); 
		    Graphics2D g=bgimage.createGraphics(); 
	        g.drawImage(src,0,0,wideth,height,null); 
	        g.setColor(Color.WHITE); 
	        g.setFont(new Font("黑体",Font.BOLD,50));
	        int xoffset = 75;
	        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	        g.drawString(uev.getName(),225,132);
	        g.drawString("[共"+quantity+"张]", wideth-95-120-quantity.toString().length()*30, 132);
	        g.setColor(new Color(245,222,195)); 
	        g.setFont(new Font("宋体",Font.PLAIN,35));
	        int y = 430;
	        int ystep = 40;
	        if(uev.getDescription().length()>21) {
	        	y = 410;
	        }

	        if(uev.getFailureDatetime() != null) {
	        	/*if(uev.getEffectDatetime() != null) {
	        		g.drawString("使用日期："+DateFormatUtils.format(uev.getEffectDatetime(),"yyyy-MM-dd")+"至"+DateFormatUtils.format(uev.getFailureDatetime(),"yyyy-MM-dd"),xoffset,y); 
	        	} else*/ {
	        		g.drawString("截止日期:"+DateFormatUtils.format(uev.getFailureDatetime(),"yyyy-MM-dd"),xoffset,y); 
	        	}
	        	y+=ystep;
	        }
	        if(uev.getDescription() != null) {
	        	String description = uev.getDescription();
	        	boolean toolong = description.length()>21;
	        	String firstline = toolong?description.substring(0, 21):description;
	        	g.drawString("领取地点:"+firstline,xoffset,y);
	        	y+=ystep;
	        	if(toolong) {
		        	String secondline = description.substring(21);
		        	if(StringUtils.isNotBlank(secondline)) {
		        		g.drawString(secondline, 230, y);
		        		y+=ystep;
		        	}
	        	}   	
	        }
	        int barcodeWidth = 800;//30*content.length();
	        int barcodeHeight = 100;
			matrix = new MultiFormatWriter().encode(
			        content, BarcodeFormat.CODE_128, barcodeWidth,
			        barcodeHeight, hints);
			BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix, new MatrixToImageConfig(MatrixToImageConfig.BLACK,MatrixToImageConfig.WHITE));//0x80FFFFFF));
			//g.fillRect(100,293,880,150);
	        g.drawImage(image, (wideth-barcodeWidth)/2, 195, barcodeWidth, barcodeHeight, null);
	        g.setColor(Color.WHITE); 
	        g.setFont(new Font("Calibri",Font.PLAIN,32));
	        StringBuilder strBuilder = new StringBuilder();
	        for(int i=0;i<content.length();i++) {
	        	strBuilder = strBuilder.append(content.charAt(i)).append("        ");
	        }
	        		
	        g.drawString(strBuilder.toString(), (wideth-barcodeWidth)/2+barcodeWidth/8, 325+20);
	        g.dispose(); 
	        Path opath = Paths.get(ROOT_DIR+outfile);
	        File odir = opath.getParent().toFile();
	        if(!odir.exists()) {
	        	odir.mkdirs();
	        }
	        if (!ImageIO.write(bgimage, "PNG", opath.toFile())) {
	        	System.out.println("ImageIO.write failed");
	        	return null;
	        }
			return outfile;
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static String generatorEV(UserEVoucher uev, String content, String background, String outfile) {
		System.out.println(content+" "+background+" "+outfile);
		File fileOne = new File(ROOT_DIR+background);
		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
	    /*if (config.errorCorrectionLevel != null) {
	      hints.put(EncodeHintType.ERROR_CORRECTION, config.errorCorrectionLevel);
	    }*/
		BitMatrix matrix;
		try {
			Image src = ImageIO.read(fileOne); 
		    int wideth=480;//src.getWidth(null); 
		    int height=270;//src.getHeight(null); 
			
		    BufferedImage bgimage=new BufferedImage(wideth,height,BufferedImage.TYPE_USHORT_555_RGB); 
		    Graphics g=bgimage.createGraphics(); 
	        g.drawImage(src,0,0,wideth,height,null); 
	        g.setColor(Color.YELLOW); 
	        g.setFont(new Font("宋体",Font.BOLD,30));  
	        int xoffset = 40;
	        g.drawString(uev.getName(),(wideth-uev.getName().length()*g.getFont().getSize())/2,40); 
	        g.setColor(Color.ORANGE); 
	        g.setFont(new Font("宋体",Font.PLAIN,18)); 
	        int y = 70;
	        int ystep = 25;
	        if(uev.getMoney()!=null) {
	        	if(uev.getMinamount()!=null) {
	        		g.drawString("满"+uev.getMinamount()+"元减"+uev.getMoney()+"元", xoffset, y);
	        	} else {
	        		g.drawString("价值："+uev.getMoney()+"元", xoffset, y);
	        	}
	        	y+=ystep;
	        }
	        if(uev.getFailureDatetime() != null) {
	        	if(uev.getEffectDatetime() != null) {
	        		g.drawString("使用日期："+DateFormatUtils.format(uev.getEffectDatetime(),"yyyy-MM-dd")+"至"+DateFormatUtils.format(uev.getFailureDatetime(),"yyyy-MM-dd"),xoffset,y); 
	        	} else {
	        		g.drawString("使用期至："+DateFormatUtils.format(uev.getFailureDatetime(),"yyyy-MM-dd"),xoffset,y); 
	        	}
	        	y+=ystep;
	        }
	        if(uev.getDescription() != null) {
	        	String description = uev.getDescription();
	        	String[] descs = description.split("\r\n");
	        	
	        	g.drawString("使用说明:"+descs[0],xoffset,y);
	        	y+=ystep;
	        	for(int i = 1; i<descs.length; i++) {
	        		g.drawString(descs[i], xoffset, y);
	        		y+=ystep;
	        	}	        	
	        }
	        int barcodeWidth = 30*content.length();
			matrix = new MultiFormatWriter().encode(
			        content, BarcodeFormat.CODE_93, barcodeWidth,
			        60, hints);
			BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix, new MatrixToImageConfig(MatrixToImageConfig.BLACK,0x80FFFFFF));

	        g.drawImage(image, (wideth-barcodeWidth)/2, height-85, barcodeWidth, 60, null);
	        g.setColor(Color.WHITE); 
	        g.setFont(new Font("Calibri",Font.PLAIN,18)); 
	        StringBuilder strBuilder = new StringBuilder();
	        for(int i=0;i<content.length();i++) {
	        	strBuilder = strBuilder.append(content.charAt(i)).append("   ");
	        }
	        		
	        g.drawString(strBuilder.toString(), (wideth-barcodeWidth)/2+25, height-10);
	        g.dispose(); 
	        Path opath = Paths.get(ROOT_DIR+outfile);
	        File odir = opath.getParent().toFile();
	        if(!odir.exists()) {
	        	odir.mkdirs();
	        }
	        if (!ImageIO.write(bgimage, "PNG", opath.toFile())) {
	        	System.out.println("ImageIO.write failed");
	        	return null;
	        }
			return outfile;
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static void main(String[] args) {
		SNGenerator snGen = new SNGenerator(1, 2, 8, 0);
		UserEVoucher uev = new UserEVoucher();
		String SN = snGen.generateSN(1,1,1);
		uev.setSn(Long.valueOf(SN));
		uev.setName("全球上网卡提取券");
		uev.setEffectDatetime(new Date());
		SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			String endtime = format.format(uev.getEffectDatetime().getTime()+30*86400000L);
			System.out.println(endtime);
			uev.setFailureDatetime(format.parse(endtime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ROOT_DIR = "F:\\";
		uev.setBackground("Voucher_member.png");
		//uev.setUserid(order.getUserid());
		uev.setRepeatable(false);
		uev.setSn(Long.valueOf(SN));
		uev.setOrderid(1L);
		uev.setEvoucherid(1L);
		uev.setMoney(50.0);
		//网点名+产品名
		uev.setDescription("浙江省杭州市余杭区海创园顺帆科技园区荆长路768号5#703");
		String imagefile = "images/"+DateFormatUtils.format(uev.getEffectDatetime(),"yyyyMM")+"/evouchers_img/"+SN+".PNG";
		generatorMemberEV(uev,SN,uev.getBackground(),imagefile);
	}
}
