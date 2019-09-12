package galco.fascor.utils;

import galco.fascor.messages.Message;
import galco.fascor.messages.MessageAbs;
import galco.fascor.messages.Message_0140;
import galco.fascor.messages.Message_1110;
import galco.fascor.messages.Message_1210;
import galco.fascor.messages.Message_1220;
import galco.fascor.messages.Message_1310;
import galco.fascor.messages.Message_1320;
import galco.fascor.messages.Message_2015;
import galco.fascor.messages.Message_3052;
import galco.fascor.messages.Message_3057;
import galco.fascor.requests.control.FascorInboundMessageHandler;
import galco.fascor.requests.control.POReceiptHandler;
import galco.fascor.wds_requests.WDSFascorRequest;
import galco.fascor.wds_requests.WDSFascorRequestCommon;
import galco.fascor.wds_requests.WDSFascorRequest_0140;
import galco.fascor.wds_requests.WDSFascorRequest_1110;
import galco.fascor.wds_requests.WDSFascorRequest_1210;
import galco.fascor.wds_requests.WDSFascorRequest_1220;
import galco.portal.config.Parms;
import galco.portal.control.ControlServlet;
import galco.portal.db.DBConnector;
import galco.portal.exception.PortalException;
import galco.portal.utils.JDBCUtils;
import galco.portal.utils.Utils;
import galco.portal.wds.dao.Codes;
import galco.portal.wds.dao.Part;
import galco.portal.wds.dao.Part_img;
import galco.portal.wds.dao.Ps_parm_data;
import galco.portal.wds.dao.Ps_rules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class FasUtils {
	private static Logger log = Logger.getLogger(FasUtils.class);

	public static void printMessageFields(Message message) {
		for (int i = 0; i < message.getFieldsHM().size(); i++) {
			log.debug(message.getFieldsHM().get(i) + " " + message.getStartingPosHM().get(i) + " " + message.getLengthsHM().get(i));
		}
	}

	// ----------------------------------------------------------------------------------------------------

	public static final int FF_DEFAULT = 0;
	public static final int FF_STRING_NULLS_TO_BLANKS = 1;
	public static final int FF_BOOLEAN_TO_Y_OR_N = 2;
	public static final int FF_DECIMAL_WITH_TWO_DECIMALS_AND_IMPLIED_DECIMAL = 3;
	public static final int FF_DECIMAL_WITH_FIVE_DECIMALS_AND_IMPLIED_DECIMAL = 4;

	/*
    public static void main(String[] args) {
    	System.out.println("***" + formatField(new Integer(12), 8, FF_DECIMAL_WITH_FIVE_DECIMALS_AND_IMPLIED_DECIMAL) + "***");
    }
    */

	public static String formatField(Object src, int length, int format) {
		if ((format == FF_DEFAULT) || (format == FF_STRING_NULLS_TO_BLANKS)) {
			if (src == null) {
				return String.format("%-" + length + "s", "");
			}

			String curLine;
			if (src instanceof String) {
				curLine = (String) src;
			} else {
				curLine = src.toString();
			}

			if (curLine.length() < length) {
				return String.format("%-" + length + "s", curLine);
			} else if (curLine.length() == length) {
				return curLine;
			} else {
				return curLine.substring(0, length);
			}
		} else if (format == FF_BOOLEAN_TO_Y_OR_N) {
			if (src == null) {
				return "N";
			} else {
				if (src instanceof Boolean) {
					return ((((Boolean) src) == true)?"Y":"N");
				} else {
					throw new RuntimeException("Field is not Boolean, src: " + src);
				}
			}
		} else if ((format == FF_DECIMAL_WITH_TWO_DECIMALS_AND_IMPLIED_DECIMAL) || (format == FF_DECIMAL_WITH_FIVE_DECIMALS_AND_IMPLIED_DECIMAL)) {
			String curLine = "";

			if (src instanceof Double) {
				if (format == FF_DECIMAL_WITH_TWO_DECIMALS_AND_IMPLIED_DECIMAL) {
					curLine = DECIMAL_FORMAT_WITH_2_DECIMAL_PLACES.format((Double) src).replace(".", "");
				} else if (format == FF_DECIMAL_WITH_FIVE_DECIMALS_AND_IMPLIED_DECIMAL) {
					curLine = DECIMAL_FORMAT_WITH_5_DECIMAL_PLACES.format((Double) src).replace(".", "");
				}
			} else if (src instanceof Float) {
				if (format == FF_DECIMAL_WITH_TWO_DECIMALS_AND_IMPLIED_DECIMAL) {
					curLine = DECIMAL_FORMAT_WITH_2_DECIMAL_PLACES.format((Float) src).replace(".", "");
				} else if (format == FF_DECIMAL_WITH_FIVE_DECIMALS_AND_IMPLIED_DECIMAL) {
					curLine = DECIMAL_FORMAT_WITH_5_DECIMAL_PLACES.format((Float) src).replace(".", "");
				}
			} else if (src instanceof Integer) {
				if (format == FF_DECIMAL_WITH_TWO_DECIMALS_AND_IMPLIED_DECIMAL) {
					curLine = DECIMAL_FORMAT_WITH_2_DECIMAL_PLACES.format((Integer) src).replace(".", "");
				} else if (format == FF_DECIMAL_WITH_FIVE_DECIMALS_AND_IMPLIED_DECIMAL) {
					curLine = DECIMAL_FORMAT_WITH_5_DECIMAL_PLACES.format((Integer) src).replace(".", "");
				}
			} else if (src instanceof Number) {
				if (format == FF_DECIMAL_WITH_TWO_DECIMALS_AND_IMPLIED_DECIMAL) {
					curLine = DECIMAL_FORMAT_WITH_2_DECIMAL_PLACES.format((Number) src).replace(".", "");
				} else if (format == FF_DECIMAL_WITH_FIVE_DECIMALS_AND_IMPLIED_DECIMAL) {
					curLine = DECIMAL_FORMAT_WITH_5_DECIMAL_PLACES.format((Number) src).replace(".", "");
				}
			} else {
				throw new RuntimeException("Field is not an allowed numeric field, src: " + src);
			}

			return FasUtils.prependZeros(curLine, length);
		}

		return "";
	}

	// ----------------------------------------------------------------------------------------------------

	public static String buildFascorMessage(Message message) {
		HashMap<Integer, String> fieldsHM = message.getFieldsHM();
		HashMap<Integer, Integer> lengthsHM = message.getLengthsHM();
		HashMap<Integer, Integer> fieldFormatsHM = message.getFieldFormatsHM();

		HashMap<String, Object> dataHM = message.getDataHM();

		HashMap<Integer, String> formattedDataHM = new HashMap<Integer, String>(100);

		int maxFieldNameLegth = Integer.MIN_VALUE;

		for (int i = 0; i < fieldsHM.size(); i++) {
			Object curData = dataHM.get(fieldsHM.get(i));

			String formattedCurData = formatField(curData, lengthsHM.get(i), fieldFormatsHM.get(i));

			formattedDataHM.put(i, formattedCurData);

			maxFieldNameLegth = Math.max(fieldsHM.get(i).length(), maxFieldNameLegth);
		}

		for (int i = 0; i < formattedDataHM.size(); i++) {
			// log.debug(String.format("%-" + maxFieldNameLegth + "s", fieldsHM.get(i)) + " " + formattedDataHM.get(i));
		}

		String fascorMessageStr = "";
		for (int i = 0; i < formattedDataHM.size(); i++) {
			fascorMessageStr += formattedDataHM.get(i);
		}

		// log.debug("Message:");
		// log.debug(fascorMessageStr);

								// fascorMessageStr = fascorMessageStr.replaceAll("'", "''");

		return fascorMessageStr;
	}

	// ----------------------------------------------------------------------------------------------------

	public static void convertValuesToStringsAddMap(HashMap<String, Object> copyThis, HashMap<String, String> copyTo) {
		Iterator<Entry<String, Object>> it = copyThis.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, Object> pair = (Map.Entry<String, Object>) it.next();
	        copyTo.put(pair.getKey(), pair.getValue().toString());
	    }
	}

	// ----------------------------------------------------------------------------------------------------

	public static DecimalFormat DECIMAL_FORMAT_WITH_2_DECIMAL_PLACES = new DecimalFormat("0.00");
	public static DecimalFormat DECIMAL_FORMAT_WITH_5_DECIMAL_PLACES = new DecimalFormat("0.00000");

    public static final int TD_NULL_TO_BLANKS = 0b1;
    public static final int TD_BOOLEAN_TO_Y_OR_N = 0b10;
    public static final int TD_DECIMAL_WITH_TWO_DECIMALS_AND_IMPLIED_DECIMAL = 0b100;
    public static final int TD_TRIM_DATA = 0b1000;
    public static final int TD_DECIMAL_WITH_FIVE_DECIMALS_AND_IMPLIED_DECIMAL = 0b10000;

	public static String transformData(Object o) {
		return 	transformData(o, TD_NULL_TO_BLANKS | TD_BOOLEAN_TO_Y_OR_N | TD_TRIM_DATA);
		// return 	transformData(o, TD_NULL_TO_BLANKS | TD_BOOLEAN_TO_Y_OR_N | TD_DECIMAL_WITH_TWO_DECIMALS_AND_IMPLIED_DECIMAL | TD_TRIM_DATA);
	}

	public static String transformData(Object o, int format) {
        boolean convertNullToBlanks = ((format & TD_NULL_TO_BLANKS) != 0);
        boolean convertBooleanToYOrN = ((format & TD_BOOLEAN_TO_Y_OR_N) != 0);
        boolean convertNumberFieldsToADecimalWithTwoDecimalsAndImpliedDecimal = ((format & TD_DECIMAL_WITH_TWO_DECIMALS_AND_IMPLIED_DECIMAL) != 0);
        boolean trimData = ((format & TD_TRIM_DATA) != 0);
        boolean convertNumberFieldsToADecimalWithFiveDecimalsAndImpliedDecimal = ((format & TD_DECIMAL_WITH_FIVE_DECIMALS_AND_IMPLIED_DECIMAL) != 0);


		if (o == null) {
			if ((convertBooleanToYOrN == true) && (o instanceof Boolean)) {
				return "N";
			} else {
				if (convertNullToBlanks == true) {
					return "";
				} else {
					return "null";
				}
			}
		} else if (o instanceof Boolean) {
			if (convertBooleanToYOrN == true) {
				return (((Boolean) o)?"Y":"N");
			}
		} else if (o instanceof Double) {
			if (convertNumberFieldsToADecimalWithTwoDecimalsAndImpliedDecimal == true) {
				return DECIMAL_FORMAT_WITH_2_DECIMAL_PLACES.format((Double) o).replace(".", "");
			} else if (convertNumberFieldsToADecimalWithFiveDecimalsAndImpliedDecimal == true) {
				return DECIMAL_FORMAT_WITH_5_DECIMAL_PLACES.format((Double) o).replace(".", "");
			}
		} else if (o instanceof Float) {
			if (convertNumberFieldsToADecimalWithTwoDecimalsAndImpliedDecimal == true) {
				return DECIMAL_FORMAT_WITH_2_DECIMAL_PLACES.format((Float) o).replace(".", "");
			} else if (convertNumberFieldsToADecimalWithFiveDecimalsAndImpliedDecimal == true) {
				return DECIMAL_FORMAT_WITH_5_DECIMAL_PLACES.format((Float) o).replace(".", "");
			}
		} else if (o instanceof Integer) {
			if (convertNumberFieldsToADecimalWithTwoDecimalsAndImpliedDecimal == true) {
				return DECIMAL_FORMAT_WITH_2_DECIMAL_PLACES.format((Integer) o).replace(".", "");
			} else if (convertNumberFieldsToADecimalWithFiveDecimalsAndImpliedDecimal == true) {
				return DECIMAL_FORMAT_WITH_5_DECIMAL_PLACES.format((Integer) o).replace(".", "");
			}
		} else if (o instanceof Number) {
			if (convertNumberFieldsToADecimalWithTwoDecimalsAndImpliedDecimal == true) {
				return DECIMAL_FORMAT_WITH_2_DECIMAL_PLACES.format((Number) o).replace(".", "");
			} else if (convertNumberFieldsToADecimalWithFiveDecimalsAndImpliedDecimal == true) {
				return DECIMAL_FORMAT_WITH_5_DECIMAL_PLACES.format((Number) o).replace(".", "");
			}
		} else if ((o instanceof String) == false) {
			log.debug("DANGER\nDANGER\nDANGER\nDANGER\nDANGER\nDANGER\nDANGER\nDANGER\nDANGER\nDANGER\nDANGER\nDANGER\n");
			log.debug("Wrong class: " + o.getClass());
			return o.toString();
		}

		if (trimData == true) {
			return o.toString().trim();
		} else {
			return o.toString();
		}
	}

	public static boolean isBitSet(int source, int position) {
		return (((source >> position) & 1) == 1);
	}

	// ----------------------------------------------------------------------------------------------------

	public static double getNumberAsDouble(Object o) {
		if (o == null) {
			return Double.NaN;
		} else {
			if(o instanceof Double) {
				return (Double) o;
			} if(o instanceof Float) {
				return (Float) o;
			} if(o instanceof Number) {
				return ((Number) o).doubleValue();
			} if(o instanceof Integer) {
				return (Integer) o;
			} if(o instanceof Character) {
				return (Character) o;
			} else {
				return Double.NaN;
			}
		}
	}

    // -------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------

	public static String getSmallImagePath(HttpServletRequest request, DBConnector dbConnector, String part_num) throws PortalException {
		ArrayList<Part> partAL = Part.getPart(dbConnector, part_num);
	    if ((partAL == null) || (partAL.size() == 0)) {
	    	return null;
	    }
	    Part part = partAL.get(0);

	    return 	getSmallImagePath(request, dbConnector, part);
	}

	public static String getSmallImagePath(HttpServletRequest request, DBConnector dbConnector, Part part) throws PortalException {
		String contextPath = request.getSession().getServletContext().getRealPath(File.separator);
		if (contextPath.length() > 0) {
			contextPath = contextPath.substring(0, contextPath.length() - 1);
		}
		return getSmallImagePath(contextPath, dbConnector, part);
	}

	public static String getSmallImagePath(String contextPath, DBConnector dbConnector, String part_num) throws PortalException {
		ArrayList<Part> partAL = Part.getPart(dbConnector, part_num);
	    if ((partAL == null) || (partAL.size() == 0)) {
	    	return null;
	    }
	    Part part = partAL.get(0);

	    return 	getSmallImagePath(contextPath, dbConnector, part);
	}

	public static String getSmallImagePath(String contextPath, DBConnector dbConnector, Part part) throws PortalException {
		String pic_src;
		boolean series_img;

		series_img = false;
		pic_src = null;

		String fs_part, encoded_pic, encoded_url;

		if (part != null) {
			int rIndex = part.getPart_num().lastIndexOf("-");
			if (rIndex >= 0) {
				fs_part = img_encode(part.getPart_num().substring(0, rIndex));
			} else {
				fs_part = img_encode(part.getPart_num());
			}
			pic_src = ("/images/" + part.getSales_subcat() + "/" + fs_part + "_s.jpg").toLowerCase();

			// log.debug("pic_src 1 : " + pic_src);
			if (new File(contextPath + pic_src).exists() == false) {
				pic_src = null;
			}

			if ((pic_src == null) && (part.getFamily_code().compareTo("") != 0) && (part.getSubfamily_code().compareTo("") != 0)) {
				ArrayList<Ps_rules> ps_rulesAL = Ps_rules.getPs_rules(dbConnector,
						part.getFamily_code(),
						part.getSubfamily_code(),
						"A",
						"P");
				Ps_rules ps_rules = null;
				if ((ps_rulesAL == null) || (ps_rulesAL.size() == 0)) {
					ps_rulesAL = Ps_rules.getPs_rules(dbConnector,
							part.getFamily_code(),
							"*",
							"A",
							"P");
					if ((ps_rulesAL != null) && (ps_rulesAL.size() > 0)) {
						ps_rules = ps_rulesAL.get(0);
					}
				} else {
					ps_rules = ps_rulesAL.get(0);
				}

				if (ps_rules != null) {
					ArrayList<Ps_parm_data> ps_parm_dataAL = Ps_parm_data.getPs_parm_data(dbConnector,
							part.getPart_num(),
							ps_rules.getSeq_num());
					if ((ps_parm_dataAL != null) && (ps_parm_dataAL.size() > 0)) {
						Ps_parm_data ps_parm_data = ps_parm_dataAL.get(0);

						encoded_pic = img_encode(ps_parm_data.getParm_value());
						encoded_url = "/images/" + part.getSales_subcat().toLowerCase() + "/" + encoded_pic;

						if (get_image_cnt(dbConnector, contextPath, encoded_url, "part") > 0) {
							if (image_ok(contextPath, "/images/" + part.getSales_subcat().toLowerCase() + "/" + encoded_pic + "_s.jpg")) {
								pic_src = "/images/" + part.getSales_subcat().toLowerCase() + "/" + encoded_pic + "_s.jpg";
								// log.debug("pic_src 2 : " + pic_src);
							}
						}
					}
				}
			}

			if (pic_src == null) {
				pic_src = ("/images/" + part.getSales_subcat() + "/" + fs_part + "_s.gif").toLowerCase();
				// log.debug("pic_src 3 : " + pic_src);

				if (new File(contextPath + pic_src).exists() == false) {
					pic_src = null;
				}

				if ((pic_src == null) && (part.getSeries().compareTo("") != 0) && (part.getSeries().compareTo(fs_part) != 0))  {
					if ((part.getFamily_code().compareTo("") != 0) && (part.getSubfamily_code().compareTo("") != 0)) {
						pic_src = ("/images/" + part.getSales_subcat() + "/" + img_encode(part.getFamily_code()) + "_" + img_encode(part.getSubfamily_code()) + "_" +
								img_encode(part.getSeries()) + "_s.jpg").toLowerCase();
						// log.debug("pic_src 4 : " + pic_src);
						if (new File(contextPath + pic_src).exists() == false) {
							pic_src = null;
						} else {
							ArrayList<Part> partAL = Part.getPart(dbConnector, part.getSales_subcat(), part.getSeries());
							if ((partAL != null) && (partAL.size() > 0)) {
								series_img = true;
							} else {
								series_img = false;
							}
						}
					}
					if (pic_src == null) {
						pic_src = ("/images/" + part.getSales_subcat() + "/" + img_encode(part.getSeries()) + "_s.jpg").toLowerCase();
						// log.debug("pic_src 5 : " + pic_src);
						if (new File(contextPath + pic_src).exists() == false) {
							pic_src = pic_src.replaceAll("_s.jpg", "_s.gif");
							// log.debug("pic_src 6 : " + pic_src);
						}

						if (new File(contextPath + pic_src).exists() == false) {
							pic_src = null;
						} else {
							ArrayList<Part> partAL = Part.getPart(dbConnector, part.getSales_subcat(), part.getSeries());
							if ((partAL != null) && (partAL.size() > 0)) {
								series_img = true;
							} else {
								series_img = false;
							}
						}
					}
				}
			}
		}

		if (pic_src == null) {
			ArrayList<Part_img> part_imgAL = Part_img.getPart_img(dbConnector, part.getPart_num(), "Picture-s");
			if ((part_imgAL != null) && (part_imgAL.size() > 0)) {
				Part_img part_img = part_imgAL.get(0);
				if (image_ok(contextPath, ("/" +  (part_img.getImg_location()).toLowerCase()))) {
					pic_src = ("/" + part_img.getImg_location()).toLowerCase();
					// log.debug("pic_src 7 : " + pic_src);
				}
			}
		}
		if (pic_src == null) {
			pic_src = "/images/catalog/picture-na_s.jpg";
		}

		return contextPath + pic_src;
	}

	private static String img_encode(String pic_name) {
		return ((pic_name.toLowerCase()).trim()).replaceAll(" ", "_").replaceAll("/", "_");
	}

	private static int get_image_cnt(DBConnector dbConnector, String contextPath, String image_base, String image_type) throws PortalException {
		int cnt = 0, i;
		String pic_types = "", pic_descs = "";

		if (image_type.compareTo("Series") == 0) {
			while (true) {
				if (image_ok(contextPath, image_base + "_" + (cnt + 1) + ".jpg")) {
					cnt = cnt + 1;
				} else {
					break;
				}
			}
		} else {/* image_type = "Part" */
			ArrayList<Codes> codesAL = Codes.getCodes(dbConnector, "TECH SHEET", "P", true);
			if ((codesAL != null) && (codesAL.size() > 0)) {
				Codes codes = codesAL.get(0);
				if (image_ok(contextPath, image_base + "_" + (codes.getValid_code()).toLowerCase() + ".jpg")) {
					cnt = 1;
					pic_types = (codes.getValid_code()).toLowerCase();
					pic_descs = codes.getDescription();
				}
			}

			codesAL = Codes.getCodes_VC_NotEq(dbConnector, "TECH SHEET", "P", true);
			if ((codesAL != null) && (codesAL.size() > 0)) {
				for (Iterator<Codes> iterator = codesAL.iterator(); iterator.hasNext();) {
					Codes codes = iterator.next();
					if (image_ok(contextPath, image_base + "_" + (codes.getValid_code()).toLowerCase() + ".jpg")) {
						cnt = cnt + 1;

						if (pic_types.compareTo("") == 0) {
							pic_types = codes.getValid_code().toLowerCase();
						} else {
							pic_types = pic_types + "," + codes.getValid_code().toLowerCase();
						}
						if (pic_descs.compareTo("") == 0) {
							pic_descs = codes.getDescription();
						} else {
							pic_descs = pic_descs + "," + codes.getDescription();
						}
					}
				}
			}
		}

		return cnt;
	}

	public static boolean image_ok(String contextPath, String imgname) {
		// RETURN SEARCH(get-cgi("DOCUMENT_ROOT") + imgname) <> ?.
		return new File(contextPath + imgname).exists();
	}

    // -------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------

	public static String getImageFilePathOld(String part_num, String sales_subcat) {
		String imageFilePath;

        String stripPart;
        {	int lIndex = part_num.lastIndexOf("-");
        	if (lIndex > 0) {
                stripPart = part_num.substring(0, lIndex).toLowerCase().trim();
        	} else {
        		stripPart = part_num;
        	}
        }
        stripPart = stripPart.replaceAll(" ", "_");
        stripPart = stripPart.replaceAll("/", "_");

        imageFilePath = "/images/" + sales_subcat.toLowerCase() + "/" + stripPart;
        imageFilePath = "/var/www/www.galco.com/htdocs" + imageFilePath + "_s.jpg";

        if (new File(imageFilePath).exists() == false) {
        	// imageFilePath = "/images/catalog/picture-na_s.jpg";
        	return null;
        } else {
        	return imageFilePath;
        }
	}

	public static String getImageFilePathOld(Connection wdsConnection, String part_num) {
		try {
			ArrayList<HashMap<String, Object>> partAL = null;
			partAL = JDBCUtils.runQuery(wdsConnection, "select sales_subcat from pub.part where part_num = '" + Utils.replaceSingleQuotesIfNotNull(part_num) + "'");
			if ((partAL == null) || (partAL.size() != 1)) {
				log.debug("Part data is missing/incorrect/locked for part: " + part_num);
				return null;
			}
			HashMap<String, Object> partHM = partAL.get(0);

			String imageFilePath = FasUtils.getImageFilePathOld(part_num, (String) partHM.get("sales_subcat"));
			return imageFilePath;
		} catch (Exception e) {
			log.debug("Exception while trying to retrieve image for part_num: " + part_num);
			log.debug(e);
			return null;
		}
	}

	// ----------------------------------------------------------------------------------------------------

	public static String convertToFixedLength(String inStr, int fieldLength) {
		return String.format("%-" + fieldLength + "s", inStr);
	}

    // ----------------------------------------------------------------------------------------------------

    public static void printAHashMap(HashMap<String, Object> hashMap) {
		int maxFieldNameLegth = Integer.MIN_VALUE;

    	for (String name: hashMap.keySet()) {
			maxFieldNameLegth = Math.max(name.length(), maxFieldNameLegth);
    	}

    	for (String name: hashMap.keySet()) {
    		log.debug(String.format("%-" + maxFieldNameLegth + "s", name) + ": " + hashMap.get(name).toString());
    	}
    	log.debug("");
	}

    public static void print_AL_Of_HMs(ArrayList<HashMap<String, Object>> alHMs) {
    	for (Iterator<HashMap<String, Object>> iterator = alHMs.iterator(); iterator.hasNext();) {
			HashMap<String, Object> hashMap = iterator.next();

			printAHashMap(hashMap);

			/*
			Iterator<Map.Entry<String, Object>> it = hashMap.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry<String, Object> pair = (Map.Entry<String, Object>) it.next();
		        log.debug(pair.getKey() + " = " + pair.getValue());
		    }
		    log.debug("\n\n");
		    */
		}
    }

	// ----------------------------------------------------------------------------------------------------

    static SimpleDateFormat yyyyHyphMMHyphDDSDF = new SimpleDateFormat("yyyy-MM-dd");
    static SimpleDateFormat yyyyMMDDSDF= new SimpleDateFormat("yyyyMMdd");
    static SimpleDateFormat mmDDYYSDF= new SimpleDateFormat("MM/dd/yy");

	public static SimpleDateFormat yyyyMmDdHmMmSs_SDF = new SimpleDateFormat("yyyyMMddHHmmss");
	public static SimpleDateFormat yyyyMmDdHmMmSsSss_SDF = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	public static SimpleDateFormat mmDdYyHhMmSsSss_SDF = new SimpleDateFormat("MM/dd/yy HH:mm:ss.SSS");
	public static SimpleDateFormat eeeMmmDdHhMmSsYyyy_SDF = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");

	public static SimpleDateFormat mmDdyyyy_SDF = new SimpleDateFormat("MMddyyyy");

    public static String convertDateToCCYYMMDD(String yyyyHyphMMHyphDD) {
		try {
	    	java.util.Date dDate = yyyyHyphMMHyphDDSDF.parse(yyyyHyphMMHyphDD);
	    	String yyyyMMDD = yyyyMMDDSDF.format(dDate);
	    	return yyyyMMDD;
		} catch (ParseException e) {
			return "";
		}
    }
    public static String convertDateToCCYYMMDD(java.util.Date inputDate) {
		try {
	    	String yyyyMMDD = yyyyMMDDSDF.format(inputDate);
	    	return yyyyMMDD;
		} catch (Exception e) {
			return "";
		}
    }
    public static String convertDateToMMDDYY(java.util.Date inputDate) {
		try {
			return mmDDYYSDF.format(inputDate);
		} catch (Exception e) {
			return "";
		}
    }

    public static String convertDateTo_yyyyMmDdHmMmSs(java.util.Date inputDate) {
		try {
			return yyyyMmDdHmMmSs_SDF.format(inputDate);
		} catch (Exception e) {
			return "";
		}
    }

    public static String convertDateTo_yyyyMmDdHmMmSsSss(java.util.Date inputDate) {
		try {
			return yyyyMmDdHmMmSsSss_SDF.format(inputDate);
		} catch (Exception e) {
			return "";
		}
    }

    public static String convertDateTo_mmDdYyHhMmSsSss_DisplayFormat(java.util.Date inputDate) {
		try {
			return mmDdYyHhMmSsSss_SDF.format(inputDate);
		} catch (Exception e) {
			return "";
		}
    }

    public static String convertDateTo_eeeMmmDdHhMmSsYyyy(java.util.Date inputDate) {
		try {
			return eeeMmmDdHhMmSsYyyy_SDF.format(inputDate);
		} catch (Exception e) {
			return "";
		}
    }

    public static String convertDateTo_mmDdyyyy(java.util.Date inputDate) {
		try {
			return mmDdyyyy_SDF.format(inputDate);
		} catch (Exception e) {
			return "";
		}
    }

    public static java.util.Date addMinutes(java.util.Date inputDate, int minutesToAdd) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(inputDate);
		cal.add(Calendar.MINUTE, minutesToAdd);
		
		return cal.getTime();
    }
	
// ----------------------------------------------------------------------------------------------------

    public static String[] splitInto50ByteStrings(String ...stringParms) {
    	String concatenatedParms = "";
    	for (String curStr: stringParms) {
    		concatenatedParms += (curStr + " ");
    	}
		concatenatedParms = concatenatedParms.trim();

		String[] fiftyByteStringArray = new String[2];

		if (concatenatedParms.length() <= 50) {
			fiftyByteStringArray[0] = concatenatedParms;
			fiftyByteStringArray[1] = "";
		} else {
			int lastBlank = concatenatedParms.substring(0,  50).lastIndexOf(' ');

			if (lastBlank < 10) {
				fiftyByteStringArray[0] = concatenatedParms.substring(0,  50);
				fiftyByteStringArray[1] = concatenatedParms.substring(50);
			} else {
				fiftyByteStringArray[0] = concatenatedParms.substring(0,  lastBlank);
				fiftyByteStringArray[1] = concatenatedParms.substring(lastBlank + 1);
			}
		}

		return fiftyByteStringArray;
    }

    // ----------------------------------------------------------------------------------------------------

	public static String getSQLExceptionInfo(SQLException ex) {
		String errorInfo = null;

		for (Throwable e : ex) {
			if (e instanceof SQLException) {
				log.debug(e);

				String sqlState = ((SQLException)e).getSQLState();
				String errorCode = "" + ((SQLException)e).getErrorCode();
				String message = e.getMessage();

				errorInfo = ((errorInfo == null)?"":"; ") + "SQLState: " + sqlState + ", Error Code: " + errorCode + ", Message: " + message;

				// log.debug("SQLState: " + sqlState);
				// log.debug("Error Code: " + errorCode);
				// log.debug("Message: " + message);

				Throwable t = ex.getCause();
				while(t != null) {
					errorInfo += "; " + ("Cause: " + t);
					// log.debug("Cause: " + t);
					t = t.getCause();
				}
			}
		}

		if (errorInfo != null) {
			return errorInfo;
		} else {
			return "";
		}
	}

	// -------------------------------------------------------------------------------------------

	public static String getFascorRequestTimeInSQLServerFormat(String sequenceNumber) {
		String fascorRequestHour = "", fascorRequestAMPM = "";

		if (new Integer(sequenceNumber.substring(8, 10)) > 12) {
			fascorRequestHour =  "" + (new Integer(sequenceNumber.substring(8, 10)) - 12);
			if (fascorRequestHour.length() < 2) {
				fascorRequestHour = "0" + fascorRequestHour;
			}
			fascorRequestAMPM = "PM";
		} else if (new Integer(sequenceNumber.substring(8, 10)) == 12) {
			fascorRequestHour =  sequenceNumber.substring(8, 10);
			fascorRequestAMPM = "PM";
		} else {
			fascorRequestHour =  sequenceNumber.substring(8, 10);
			fascorRequestAMPM = "AM";
		}

		String fascorRequestTime = sequenceNumber.substring(0, 8) + " " +
								   fascorRequestHour + ":" +
								   sequenceNumber.substring(10, 12) + ":" +
								   sequenceNumber.substring(12, 14) + " " +
								   fascorRequestAMPM;

		return fascorRequestTime;
	}

	public static String getFascorRequestAction(Connection wdsConnection, String messageType, String key1, String key2, String key3) throws SQLException {
		ArrayList<HashMap<String, Object>> processedReqsAL = null;
		processedReqsAL = JDBCUtils.runQuery(wdsConnection, "select top 1 sequenceNumber, key1, key2, key3 from pub.FascorMessage where messageType = '" + messageType + "' and key1 = '" + key1 + "' and key2 = '" + key2 + "' and key3 = '" + key3 + "'");
		if ((processedReqsAL == null) || (processedReqsAL.size() == 0)) {
			return "A";
		} else {
			return "C";
		}
	}

	public static void sendMessageToFascor(Connection sqlServerConnection, String fascorMessageStr) throws SQLException {
		String sequenceNumber_yyyyMMddHHmmss = yyyyMMddHHmmss.format(new Date());
		sendMessageToFascor(sqlServerConnection, sequenceNumber_yyyyMMddHHmmss, fascorMessageStr);
	}

	public static void sendMessageToFascor(Connection sqlServerConnection, String sequenceNumber, String fascorMessageStr) throws SQLException {
		long startTime = System.currentTimeMillis();
		
		String messageType = fascorMessageStr.substring(0, 4);
		String fascorRequestTimeInSQLServerFormat = getFascorRequestTimeInSQLServerFormat(sequenceNumber);

		String sqlStatement = "INSERT INTO [dbo].[InBound] (Trans, Batch, Text, Update_Date, Update_User_ID, Update_PID) VALUES ('" + messageType + "', 'WDS', '" + Utils.replaceSingleQuotesIfNotNull(fascorMessageStr) + "', '" + fascorRequestTimeInSQLServerFormat + "', 'sa', '" + sequenceNumber.substring(8, 14) + "')";
		log.debug(sqlStatement);

		JDBCUtils.runUpdateQueryAgainstFascor_Fascor(sqlServerConnection, sqlStatement);

		String key = MessageAbs.getKeyFromFascorInboundMessage(fascorMessageStr);
		FascorInboundMessageHandler.KEYS_OF_INBOUND_MESSAGES.add(key);
		
		log.debug("Time took, in millis, to sendMessageToFascor inbound table: " + (System.currentTimeMillis() - startTime));
	}

	public static void set_FascorRequest_Processed_Flag(Connection wdsConnection, String sequenceNumber, String processed) throws SQLException {
		String sqlStatement = "update pub.FascorRequests set processed = '" + processed + "' where sequenceNumber = '" + sequenceNumber + "'";
		log.debug(sqlStatement);

		JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStatement);
	}

	public static void set_FascorRequest_Processed_Flag_CommitChanges(Connection wdsConnection, String sequenceNumber, String processed) throws SQLException {
		String sqlStatement = "update pub.FascorRequests set processed = '" + processed + "' where sequenceNumber = '" + sequenceNumber + "'";
		log.debug(sqlStatement);

		try {
			JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStatement);
		} catch (SQLException e) {
			throw e;
		}

		try {
	        JDBCUtils.commitWDSChanges_Fascor(wdsConnection);
		} catch (SQLException e) {
			JDBCUtils.rollbackWDSChanges_Fascor(wdsConnection);
			throw e;
		}
	}

	// -------------------------------------------------------------------------------------------

    public static WDSFascorRequestCommon buildWDSFascorRequest(String fascorReqStr) throws Exception  {
    	log.debug(fascorReqStr);

    	String fascorReqArr[] = fascorReqStr.split("\\|");

        HashMap<String, Object> fascorReqHM = new HashMap<String, Object>(8);

        fascorReqHM.put("sequenceNumber", fascorReqArr[0]);
        fascorReqHM.put("processed", fascorReqArr[1]);
        fascorReqHM.put("functionCode", fascorReqArr[2]);
        fascorReqHM.put("data", fascorReqArr[3]);

        if (fascorReqArr[2].substring(0,  4).compareTo("0140") == 0) {
        	return new WDSFascorRequest_0140(fascorReqHM);
        } else if (fascorReqArr[2].substring(0,  4).compareTo("1110") == 0) {
        	return new WDSFascorRequest_1110(fascorReqHM);
        } else if (fascorReqArr[2].substring(0,  4).compareTo("1210") == 0) {
        	return new WDSFascorRequest_1210(fascorReqHM);
        } else if (fascorReqArr[2].substring(0,  4).compareTo("1220") == 0) {
        	return new WDSFascorRequest_1220(fascorReqHM);
        }

        throw new RuntimeException("Bad function code: " + fascorReqArr[2]);
    }

	// -------------------------------------------------------------------------------------------

    public static final String STRING_OF_ZEROS = "00000000000000000000000000000000000000000000000000";
    public static String prependZeros(String src, int length) {
    	if (src == null) {
    		return String.format("%0" + length + "d", 0);
    	} else if (src.length() >= length) {
    		return src.substring(src.length() - length);
    	} else {
    		return (STRING_OF_ZEROS.substring(0, length - src.length()) + src);
    	}
    }

    public static String buildSQLInsertStatement(HashMap<String, Object> hm, String table) {
    	String columns = null, values = null;

    	for (String key : hm.keySet()) {
    		if (columns == null) {
        		columns = "(" + key;

        		Object value = hm.get(key);
        		if (value instanceof Number) {
            		values = "(" + value;
        		} else {
            		values = "(" + "'" + value + "'";
        		}
    		} else {
        		columns += ", " + key;

        		Object value = hm.get(key);
        		if (value instanceof Number) {
            		values += ", " + value;
        		} else {
            		values += ", " + "'" + value + "'";
        		}
    		}
    	}

    	return "insert into " + table + " " + columns + ") values " + values + ")";
    }

	// -------------------------------------------------------------------------------------------

    public static double roundTo2Decimals(double d) {
    	return Math.round(d * 100.0) / 100.0;
    }

	// -------------------------------------------------------------------------------------------

    // qqq
    public static void printHashMap(HashMap<String, Object> hashMap) {
	    for (String key : hashMap.keySet()) {
			log.debug(String.format("%-25s", key) + " " + hashMap.get(key));
		}
    }

    public static void printHashMap_SingleLine_NoColumnName(HashMap<String, Object> hashMap) {
    	String msg = "";

	    for (String key : hashMap.keySet()) {
	    	msg += hashMap.get(key) + " ";
		}

	    log.debug(msg);
    }

    public static void printArrayListOfHashMaps(ArrayList<HashMap<String, Object>> al) {
    	int[] columnMaxLengths = new int[100];
    	for (int i = 0; i < columnMaxLengths.length; i++) {
    		columnMaxLengths[i] = 1;
		}

    	for (Iterator<HashMap<String, Object>> iterator = al.iterator(); iterator.hasNext();) {
    		HashMap<String, Object> hashMap = (HashMap<String, Object>) iterator.next();

    		int i = 0;
    	    for (String key : hashMap.keySet()) {
    	    	String value;
    	    	if (hashMap.get(key) == null) {
    	    		value = "";
    	    	} else {
    	    		value = hashMap.get(key).toString();
    	    	}

   	    		columnMaxLengths[i] = Math.max(columnMaxLengths[i], value.length());

    	    	i++;
    		}
    	}

    	for (Iterator<HashMap<String, Object>> iterator = al.iterator(); iterator.hasNext();) {
    		HashMap<String, Object> hashMap = (HashMap<String, Object>) iterator.next();

        	String msg = null;
    		int i = 0;
    	    for (String key : hashMap.keySet()) {
    	    	String value;
    	    	if (hashMap.get(key) == null) {
    	    		value = "";
    	    	} else {
    	    		value = hashMap.get(key).toString();
    	    	}

    	    	value = String.format("%-" + columnMaxLengths[i] + "s", value);
    	    	if (msg != null) {
    	    		msg += " | ";
    	    	} else {
    	    		msg = "";
    	    	}
       	    	msg += value;

    	    	i++;
    		}

    	    System.out.println(msg);
    	    log.debug(msg);
    	}
    }

    // -------------------------------------------------------------------------------------------

    public static String executeShellScriptFromWindows(String hostname, String username,String password, String pathOfShellScript) throws Exception, IOException {
		ch.ethz.ssh2.Connection conn = new ch.ethz.ssh2.Connection(hostname);
		conn.connect();
		boolean isAuthenticated = conn.authenticateWithPassword(username, password);
		if (isAuthenticated == false) {
			throw new Exception("Authentication failed.");
		}

		ch.ethz.ssh2.Session sess = conn.openSession();
		sess.execCommand("sh " + pathOfShellScript);

		// System.out.println("Here is some information about the remote host:");

		InputStream stdout = new ch.ethz.ssh2.StreamGobbler(sess.getStdout());
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stdout));
		String commandOutput = "";
		while (true) {
			String curLine = bufferedReader.readLine();
			if (curLine == null) {
				break;
			}
			commandOutput += ((commandOutput.compareTo("") == 0)?curLine:(System.getProperty("line.separator") + curLine));
		}
		bufferedReader.close();

		System.out.println("ExitCode: " + sess.getExitStatus());

		sess.close();
		conn.close();

		if (sess.getExitStatus() == 0) {
			if ((commandOutput != null) && (commandOutput.indexOf("*+*success*+*") >= 0)) {
	    		return commandOutput;
			}
		}
		throw new Exception("Shell script, " + pathOfShellScript + " failed, error code:" + sess.getExitStatus());
    }

    // -------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------

    public static int executeCommandLine(final String commandLine,
    		final long timeoutMillis, String outputFileFullPath) throws IOException, InterruptedException, TimeoutException {
    	long startTime_ExecuteCommandLine = System.currentTimeMillis();
    	
    	Runtime runtime = Runtime.getRuntime();

    	Process process = runtime.exec(commandLine);

    	Worker worker = new Worker(process);
    	worker.start();

    	try {
    		worker.join(timeoutMillis);
    		if (worker.exit != null) {
    	    	log.debug("ZZYYXX - executeCommandLine took " + ((System.currentTimeMillis() - startTime_ExecuteCommandLine) / 1000) + " seconds.");

				String outputContents = FasUtils.readFileContentsIntoAString(outputFileFullPath);
				log.debug("ZZYYXX - Contents of " + outputFileFullPath + ":");
				log.debug(outputContents);

				return worker.exit;
    		} else {
        		File outputFile = new File(outputFileFullPath);
        		
        		long startTime = System.currentTimeMillis();
        		long fileSize = outputFile.length();        		
        		long fileSizePrev;
        		
        		long lastFileChangedTime = System.currentTimeMillis();
        		
        		while (true) {
        			Utils.sleep(2000);

        			if (worker.exit != null) {
            	    	log.debug("ZZYYXX - executeCommandLine took " + ((System.currentTimeMillis() - startTime_ExecuteCommandLine) / 1000) + " seconds.");

        				String outputContents = FasUtils.readFileContentsIntoAString(outputFileFullPath);
        				log.debug("ZZYYXX - Contents of " + outputFileFullPath + ":");
        				log.debug(outputContents);

            			return worker.exit;
        			}

            		if (System.currentTimeMillis() >= (startTime + (timeoutMillis + 10000))) {
            	    	log.debug("ZZYYXX - executeCommandLine didn't finish even after running for " + ((System.currentTimeMillis() - startTime_ExecuteCommandLine) / 1000) + " seconds.");
            			
        				String outputContents = FasUtils.readFileContentsIntoAString(outputFileFullPath);
        				log.debug("ZZYYXX - Contents of " + outputFileFullPath + ":");
        				log.debug(outputContents);

            			throw new TimeoutException("Process hasn't finished in " + ((timeoutMillis + 480000) / 60000)  + " minutes.");
            		}
            		
            		fileSizePrev = fileSize;
            		fileSize = outputFile.length();        		
            		if (fileSize == fileSizePrev) {
            			if (System.currentTimeMillis() >= (lastFileChangedTime + 20000)) {
	            	    	log.debug("ZZYYXX - executeCommandLine didn't finish even after running for " + ((System.currentTimeMillis() - startTime_ExecuteCommandLine) / 1000) + " seconds.");
	            			
	        				String outputContents = FasUtils.readFileContentsIntoAString(outputFileFullPath);
	        				log.debug("ZZYYXX - Contents of " + outputFileFullPath + ":");
	        				log.debug(outputContents);
	
	            			throw new TimeoutException(outputFileFullPath + " size hasn't changed in 20 seconds.");
            			}
            		} else {
                		lastFileChangedTime = System.currentTimeMillis();            			
            		}
        		}
    			// throw new TimeoutException();
    		}
    	} catch(InterruptedException ex) {
    		worker.interrupt();
    		Thread.currentThread().interrupt();
    		throw ex;
    	} finally {
    		process.destroy();
    	}
    }

	private static class Worker extends Thread {
		private final Process process;
		private Integer exit;

		private Worker(Process process) {
			this.process = process;
		}

		public void run() {
			try {
				try {
	                StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR");
                    StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT");
                    errorGobbler.start();
                    outputGobbler.start();
				} catch (Exception e) {
					log.debug("Exception in worker run", e);
				}

                log.debug("worker is beginning waiting at " + System.currentTimeMillis());
				exit = process.waitFor();
				log.debug("worker is finished waiting at " + System.currentTimeMillis());				
			} catch (InterruptedException ignore) {
				log.debug("InterruptedException in worker run method at " + System.currentTimeMillis());				
				try {
					log.debug("Going to Kill the process");
					process.destroy();
					log.debug("Killed the process");					
				} catch (Exception e) {
					log.debug("Exception occurred while trying to kill the process.", e);
				}
				return;
			}
		}
	}

    private static class StreamGobbler extends Thread {
        InputStream is;
        String type;

        StreamGobbler(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line=null;
                
                log.debug("StreamGobbler is beginning waiting " + System.currentTimeMillis());
                
                String output = "";
                while ( (line = br.readLine()) != null) {
                	output += type + " --- " + line + "\n";
                    // System.out.println(type + ">" + line);
                }
                log.debug(output);
                log.debug("StreamGobbler is finished waiting " + System.currentTimeMillis());
                
            } catch (IOException ioe) {
            	ioe.printStackTrace();
            }
       	}
    }

    // -------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------

    public static int executeCommandLineO(final String commandLine,
    		final long timeoutMillis, String outputFileFullPath) throws IOException, InterruptedException, TimeoutException {
    	long startTime_ExecuteCommandLine = System.currentTimeMillis();
    	
    	Runtime runtime = Runtime.getRuntime();

    	Process process = runtime.exec(commandLine);

    	Worker worker = new Worker(process);
    	worker.start();

    	try {
    		worker.join(timeoutMillis);
    		if (worker.exit != null) {
    	    	log.debug("ZZYYXX - executeCommandLine took " + ((System.currentTimeMillis() - startTime_ExecuteCommandLine) / 1000) + " seconds.");

				String outputContents = FasUtils.readFileContentsIntoAString(outputFileFullPath);
				log.debug("ZZYYXX - Contents of " + outputFileFullPath + ":");
				log.debug(outputContents);

				return worker.exit;
    		} else {
    			
    			
    			
    			
    			
    			
    			
    			
    			
    			
    			
    			
        		File outputFile = new File(outputFileFullPath);
        		
        		long startTime = System.currentTimeMillis();
        		long fileSize = outputFile.length();        		
        		long fileSizePrev;
        		
        		while (true) {
        			Utils.sleep(20000);

        			if (worker.exit != null) {
            	    	log.debug("ZZYYXX - executeCommandLine took " + ((System.currentTimeMillis() - startTime_ExecuteCommandLine) / 1000) + " seconds.");

        				String outputContents = FasUtils.readFileContentsIntoAString(outputFileFullPath);
        				log.debug("ZZYYXX - Contents of " + outputFileFullPath + ":");
        				log.debug(outputContents);

            			return worker.exit;
        			}

            		if (System.currentTimeMillis() >= (startTime + 480000)) {
            	    	log.debug("ZZYYXX - executeCommandLine didn't finish even after running for " + ((System.currentTimeMillis() - startTime_ExecuteCommandLine) / 1000) + " seconds.");
            			
        				String outputContents = FasUtils.readFileContentsIntoAString(outputFileFullPath);
        				log.debug("ZZYYXX - Contents of " + outputFileFullPath + ":");
        				log.debug(outputContents);

            			throw new TimeoutException("Process hasn't finished in " + ((timeoutMillis + 480000) / 60000)  + " minutes.");
            		}
            		
            		fileSizePrev = fileSize;
            		fileSize = outputFile.length();        		
            		if (fileSize == fileSizePrev) {
            	    	log.debug("ZZYYXX - executeCommandLine didn't finish even after running for " + ((System.currentTimeMillis() - startTime_ExecuteCommandLine) / 1000) + " seconds.");
            			
        				String outputContents = FasUtils.readFileContentsIntoAString(outputFileFullPath);
        				log.debug("ZZYYXX - Contents of " + outputFileFullPath + ":");
        				log.debug(outputContents);

            			throw new TimeoutException(outputFileFullPath + " size hasn't changed in 20 seconds.");
            		}
        		}
    			
    			
    			
    			
    			
    			
    			
    			
    			
    			
    			
    			
    			
    			
    			// throw new TimeoutException();
    		}
    	} catch(InterruptedException ex) {
    		worker.interrupt();
    		Thread.currentThread().interrupt();
    		throw ex;
    	} finally {
    		process.destroy();
    	}
    }

    // -------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------

    public static int executeCommandLineO(final String commandLine,
    		final long timeoutMillis) throws IOException, InterruptedException, TimeoutException {
    	Runtime runtime = Runtime.getRuntime();

    	Process process = runtime.exec(commandLine);

    	WorkerO worker = new WorkerO(process);
    	worker.start();

    	try {
    		worker.join(timeoutMillis);
    		if (worker.exit != null) {
    			return worker.exit;
    		} else {
    			throw new TimeoutException();
    		}
    	} catch(InterruptedException ex) {
    		worker.interrupt();
    		Thread.currentThread().interrupt();
    		throw ex;
    	} finally {
    		process.destroy();
    	}
    }

	private static class WorkerO extends Thread {
		private final Process process;
		private Integer exit;

		private WorkerO(Process process) {
			this.process = process;
		}

		public void run() {
			try {
				try {
	                StreamGobblerO errorGobbler = new StreamGobblerO(process.getErrorStream(), "ERROR");
                    StreamGobblerO outputGobbler = new StreamGobblerO(process.getInputStream(), "OUTPUT");
                    errorGobbler.start();
                    outputGobbler.start();
				} catch (Exception e) {
					log.debug("Exception in worker run", e);
				}

                log.debug("worker is beginning waiting at " + System.currentTimeMillis());
				exit = process.waitFor();
				log.debug("worker is finished waiting at " + System.currentTimeMillis());				
			} catch (InterruptedException ignore) {
				log.debug("InterruptedException in worker run method at " + System.currentTimeMillis());				
				try {
					log.debug("Going to Kill the process");
					process.destroy();
					log.debug("Killed the process");					
				} catch (Exception e) {
					log.debug("Exception occurred while trying to kill the process.", e);
				}
				return;
			}
		}
	}

    private static class StreamGobblerO extends Thread {
        InputStream is;
        String type;

        StreamGobblerO(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line=null;
                
                log.debug("StreamGobbler is beginning waiting " + System.currentTimeMillis());
                
                String output = "";
                while ( (line = br.readLine()) != null) {
                	output += type + " --- " + line + "\n";
                    // System.out.println(type + ">" + line);
                }
                log.debug(output);
                log.debug("StreamGobbler is finished waiting " + System.currentTimeMillis());
                
            } catch (IOException ioe) {
            	ioe.printStackTrace();
            }
       	}
    }

    // -------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------

	public static String readFileContentsIntoAString(String filePath) throws IOException, FileNotFoundException {
	    BufferedReader reader = new BufferedReader(new FileReader(filePath));

	    String fileContents = "";

	    String curLine = null;
        while((curLine = reader.readLine()) != null) {
        	fileContents += ((fileContents.compareTo("") == 0)?curLine:(System.getProperty("line.separator") + curLine));
        }

        reader.close();

        return fileContents;
	}

    // -------------------------------------------------------------------------------------------

	public static long get_IDENT_CURRENT(Connection sqlServerConnection, String tableName) throws SQLException {
    	long identity;
    	ArrayList<HashMap<String, Object>> identALInBound = JDBCUtils.runQuery(sqlServerConnection, "SELECT IDENT_CURRENT ('" + tableName + "') as IDENT_CURRENT");
    	if ((identALInBound == null) || (identALInBound.size() != 1)) {
    		throw new RuntimeException("SELECT IDENT_CURRENT ('InBound') failed.");
    	}
    	identity = ((Number) (identALInBound.get(0)).get("IDENT_CURRENT")).longValue();
    	log.debug("IDENT_CURRENT of " + tableName + ": " + identity);

    	return identity;
	}

    // -------------------------------------------------------------------------------------------

	public static String getFascorErrorDescription(Connection sqlServerConnection, String return_Code) throws SQLException {
    	ArrayList<HashMap<String, Object>> pferrormsgAL = JDBCUtils.runQuery(sqlServerConnection, "select body_text FROM [dbo].[pferrormsg] where error_num = " + return_Code);
    	if ((pferrormsgAL == null) || (pferrormsgAL.size() != 1)) {
    		return "";
    	}
    	return (String) (pferrormsgAL.get(0)).get("body_text");
	}

    // -------------------------------------------------------------------------------------------

	public static SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
	public static SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	public static SimpleDateFormat eeeMmmDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");

	public static long generatedRequestNo = 1;

 	public static WDSFascorRequestCommon createFascorRequest(Connection wdsConnection, String functionCode, String key) throws SQLException {
 		Date curDate = new Date();

 		if (generatedRequestNo > 99999) {
 			generatedRequestNo = 1;
 		}

 		// String sequenceNumber = yyyyMMddHHmmssSSS.format(curDate) + "-" + (generatedRequestNo++) + "-" + ((int) Math.random() * 1000);
 		String sequenceNumber = yyyyMMddHHmmssSSS.format(curDate) + "-" + (generatedRequestNo++) + "-" + ((int) (Math.random() * 999999));

 		String transTime = String.format("%1$30s", eeeMmmDateFormat.format(curDate) + " GNRTD");

 		String sqlStatement = "insert into pub.FascorRequests (sequenceNumber, processed, functionCode, data, \"key\") values ('" + sequenceNumber + "', 'N', '" + functionCode + "', '" + Utils.replaceSingleQuotesIfNotNull(transTime) + "', '" + Utils.replaceSingleQuotesIfNotNull(key) + "')";
		log.debug(sqlStatement);
		JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStatement);

		return new WDSFascorRequestCommon(sequenceNumber, "N", functionCode.substring(0, 4), functionCode.substring(4, 5), transTime, transTime, key);
	}

 	public static WDSFascorRequestCommon createFascorRequest_O(Connection wdsConnection, String functionCode, String dataWithoutTime) throws SQLException {
 		Date curDate = new Date();

 		if (generatedRequestNo > 99999) {
 			generatedRequestNo = 1;
 		}

 		// String sequenceNumber = yyyyMMddHHmmssSSS.format(curDate) + ((int) (99999 * Math.random()));
 		String sequenceNumber = yyyyMMddHHmmssSSS.format(curDate) + "-" + (generatedRequestNo++) + "-" + ((int) Math.random() * 1000);

 		String data = String.format("%1$30s", eeeMmmDateFormat.format(curDate) + " GNRTD") + dataWithoutTime;

 		String sqlStatement = "insert into pub.FascorRequests (sequenceNumber, processed, functionCode, data) values ('" + sequenceNumber + "', 'N', '" + functionCode + "', '" + Utils.replaceSingleQuotesIfNotNull(data) + "')";
		log.debug(sqlStatement);
		JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStatement);

		// Commenting out, compile error, won't work
		// return new WDSFascorRequestCommon(sequenceNumber, "N", functionCode.substring(0, 4), functionCode.substring(4, 5), data, data.substring(0, 30).trim());
		if (true) throw new RuntimeException("Tappu code");
		return null;
	}

	// -------------------------------------------------------------------------------------------
 	
 	public static long startTime_TrackTime = System.currentTimeMillis();
	public static void trackTimeS() {
		startTime_TrackTime = System.currentTimeMillis();
	}
	public static long trackTimeE() {
		long timeTook = System.currentTimeMillis() - startTime_TrackTime;
		startTime_TrackTime = System.currentTimeMillis();
		
		return timeTook;
	}

	// -------------------------------------------------------------------------------------------
 	
	public static void main(String[] args) {
		// 30 000 000 000
		long iterationStartNanoTime = System.nanoTime();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println(System.nanoTime() - iterationStartNanoTime);
		
		System.exit(0);
		
		/*
		ArrayList<String> guptaPadaaluAL = galco.portal.utils.Utils.loadFileIntoArrayList("C:\\0Ati\\ZGuptaPadaalu.txt");

		ControlServlet.GUPTA_GLUU = guptaPadaaluAL.get(0);
		ControlServlet.GUPTA_PORTAL_TALLI_GUPTAPADAM = guptaPadaaluAL.get(1);
		ControlServlet.GUPTA_PASS_DISPLAY_GUPTAPADAM = guptaPadaaluAL.get(2);

		System.out.println("ControlServlet.GUPTA_GLUU: " + ControlServlet.GUPTA_GLUU);
		System.out.println("ControlServlet.GUPTA_PORTAL_TALLI_GUPTAPADAM: " + ControlServlet.GUPTA_PORTAL_TALLI_GUPTAPADAM);
		System.out.println("ControlServlet.GUPTA_PASS_DISPLAY_GUPTAPADAM: " + ControlServlet.GUPTA_PASS_DISPLAY_GUPTAPADAM);

		System.exit(0);



		galco.portal.utils.Utils.sendMailJustLogError("sati@galco.com", "WDSFascorIntegration@galco.com", "Test", "Test");
		System.exit(0);

		*/

		DBConnector dbConnector8 = null;
		try {
			dbConnector8 = new DBConnector(false);

			ArrayList<HashMap<String, Object>> oitemAL = null;

			oitemAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select order_num, line_num, part_num from pub.oitem where order_num = 'W991781' WITH (NOLOCK)");

        	if ((oitemAL != null) && (oitemAL.size() > 0)) {
        		for (Iterator<HashMap<String, Object>> iterator = oitemAL.iterator(); iterator.hasNext();) {
					HashMap<String, Object> hashMap = (HashMap<String, Object>) iterator.next();

					System.out.println(hashMap.get("order_num"));
					System.out.println(hashMap.get("line_num"));
					System.out.println(hashMap.get("part_num"));
				}
        	}







			//int currTransactionIsolation = dbConnector8.getConnectionWDS().getTransactionIsolation();
			//dbConnector8.getConnectionWDS().setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			// ArrayList<HashMap<String, Object>> fascorRequestsAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select sequenceNumber, processed, functionCode, data from pub.FascorRequests where processed <> 'Y' ORDER BY data, sequenceNumber, functionCode WITH (READPAST NOWAIT)");
			// Utils.print_AL_Of_HMs(fascorRequestsAL);
			//dbConnector8.getConnectionWDS().setTransactionIsolation(currTransactionIsolation);

			//SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
        	//String curTimeMinus3Minutes_As_SequenceNumber = yyyyMMddHHmmss.format(new Date(System.currentTimeMillis() - 180000));
            //ArrayList<HashMap<String, Object>> fascorRequestsAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select sequenceNumber, processed, functionCode, data from pub.FascorRequests where processed <> 'Y' and sequenceNumber <= '" + curTimeMinus3Minutes_As_SequenceNumber + "'");
            //Utils.print_AL_Of_HMs(fascorRequestsAL);


			// Message_0140 message_0140 = new Message_0140(dbConnector8.getConnectionWDS(), "12736", "A");
			// String fascorMessageStr = Utils.buildFascorMessage(message_0140);

			// Message_1110 message_1110 = new Message_1110(dbConnector8.getConnectionWDS(), "PC1300-DURA", "A");
			// String fascorMessageStr = Utils.buildFascorMessage(message_1110);

			// Message_1210 message_1210 = new Message_1210(dbConnector8.getConnectionWDS(), "1825881", "A");
			// String fascorMessageStr = Utils.buildFascorMessage(message_1210);

			// Message_1220 message_1220 = new Message_1220(dbConnector8.getConnectionWDS(), "1825881", 1, "A");
			// String fascorMessageStr = Utils.buildFascorMessage(message_1220);

			// Message_1310 message_1310 = new Message_1310(dbConnector8.getConnectionWDS(), "AA88513", "A");
			// String fascorMessageStr = Utils.buildFascorMessage(message_1310);

			// Message_1320 message_1320 = new Message_1320(dbConnector8.getConnectionWDS(), "AA88513", 1, "A");
			// String fascorMessageStr = Utils.buildFascorMessage(message_1320);

			// POReceiptHandler.receivePO(dbConnector8.getConnectionWDS(), "1825914");






			// Message_3052 message_3052 = new Message_3052(dbConnector8.getConnectionWDS(), "410403-24awl-nbr", "G", "A");
			// String fascorMessageStr = FasUtils.buildFascorMessage(message_3052);

			// Message_3057 message_3057 = new Message_3057(dbConnector8.getConnectionWDS(), "410403-24awl-nbr", "G", 1, "A");
			// String fascorMessageStr = FasUtils.buildFascorMessage(message_3057);

			// Message_2015 message_2015 = new Message_2015(dbConnector8.getConnectionWDS(), "A876543", "410403-24awl-nbr", 14, "A");
			// String fascorMessageStr = FasUtils.buildFascorMessage(message_2015);

			// System.out.println(Message_3052.get_eng_rls_num(dbConnector8.getConnectionWDS(), "410403-24awl-nbr"));





			/*
        	String order_num = "AA99549";

        	ArrayList<Object> partsToBuildAL;
        	ArrayList<Object> quanititiesToBuildAL;
        	{
        		ArrayList<Object>[] alArr = JDBCUtils.runQuery_ReturnObjALs(dbConnector8.getConnectionWDS(), "select o.part_num, o.qty_com_asm from pub.oitem as o, pub.part as p where o.order_num = '" + order_num + "' and p.part_num = o.part_num and p.kit = 1 and o.qty_com_asm > 0");
        		partsToBuildAL = alArr[0];
        		quanititiesToBuildAL = alArr[1];
        	}

        	for (int i = 0; i < partsToBuildAL.size(); i++) {
				String part_num = (String) partsToBuildAL.get(i);
				int quanitityToBuild = (int) quanititiesToBuildAL.get(i);

				log.debug(part_num + " " + quanitityToBuild);
			}
			*/



			/*
		    JDBCUtils.UPDATE_WDS_DB = true;
			FasUtils.createFascorRequest(dbConnector8.getConnectionWDS(), "2015A", "410403-24AWL-NBR,3");
            JDBCUtils.commitWDSChanges_Fascor(dbConnector8.getConnectionWDS());
			*/



			/*
	 		String sqlStatement = "insert into pub.OrdersWaitingOnKits (order_num, sequenceNumber, part_num, quantityBuilt, origQuantity, buildComplete) values ('" + sequenceNumber + "', 'N', '" + functionCode + "', '" + data + "')";
			log.debug(sqlStatement);
			JDBCUtils.runUpdateQueryAgainstWDS_Fascor(wdsConnection, sqlStatement);

			return new WDSFascorRequestCommon(sequenceNumber, "N", functionCode.substring(0, 4), functionCode.substring(4, 5), data, data.substring(0, 30).trim());
			*/




			/*
			String receiver_nbr = "11";

			int poNumberInReceiver = 1;
			{
				ArrayList<HashMap<String, Object>> po_rcptAL = null;
				// po_rcptAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select rcvg_reportnum from pub.po_rcpt where substring(rcvg_reportnum, 1 , " + (receiver_nbr.length() + 1) + ") = '" + receiver_nbr + "-'");
				po_rcptAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select rcvg_reportnum from pub.po_rcpt where rcvg_reportnum like '" + receiver_nbr + "-%'");
				if ((po_rcptAL != null) && (po_rcptAL.size() > 0)) {
				    for (Iterator<HashMap<String, Object>> iterator4 = po_rcptAL.iterator(); iterator4.hasNext();) {
				        HashMap<String, Object> po_rcptHM = (HashMap<String, Object>) iterator4.next();

				        String prev_rcvg_reportnum = ((String) po_rcptHM.get("rcvg_reportnum"));

				        int poNo = new Integer(prev_rcvg_reportnum.substring(prev_rcvg_reportnum.indexOf("-") + 1));
				        poNumberInReceiver = Math.max(poNo, poNumberInReceiver);
				    }

				    poNumberInReceiver += 1;
				}
			}

			System.out.println("poNumberInReceiver: " + poNumberInReceiver);
			*/



			/*

			ArrayList<HashMap<String, Object>> oitemAL = null;
			oitemAL = JDBCUtils.runQuery(dbConnector8.getConnectionWDS(), "select top 1 oitem.order_num, oitem.part_num, order.pros_status[3] from pub.oitem, pub.order where oitem.part_num = '18881100003-PFNG' and oitem.backorder = 1 and oitem.order_num = order.order_num and order.date_closed IS NULL and order.on_cr_hold = 0 and order.on_hold = 0 and order.drop_ship = 0 and order.pros_status[3] <> 'v' WITH (READPAST NOWAIT)");
			FasUtils.print_AL_Of_HMs(oitemAL);
			if ((oitemAL != null) && (oitemAL.size() > 0)) {
				log.debug("Hot : Y, one order waiting for this part: " + (oitemAL.get(0).get("order_num")));
				log.debug("Hot : Y");
			} else {
				log.debug("Hot : N");
			}

			*/



			dbConnector8.closeConnections();
		} catch (SQLException e) {
			String sqlExceptionInfo = getSQLExceptionInfo(e);
			log.debug(sqlExceptionInfo);
			log.debug(e);
			// galco.portal.utils.Utils.sendMailJustLogError("sati@galco.com", "WebPortal@galco.com", "Problem in FASCOR Batch Process of " + Parms.HOST_NAME + ".", "SQLException occurred.\n" + sqlExceptionInfo);
			if (dbConnector8 != null) {
				try {
					dbConnector8.closeConnections();
				} catch (Exception | PortalException e2) {
				}
			}
		} catch (Exception e) {
			if (dbConnector8 != null) {
				try {
					dbConnector8.closeConnections();
				} catch (Exception | PortalException e2) {
				}
			}
			e.printStackTrace();
		} catch (PortalException e) {
			if (dbConnector8 != null) {
				try {
					dbConnector8.closeConnections();
				} catch (Exception | PortalException e2) {
				}
			}
			e.getE().printStackTrace();
		}
        System.exit(0);



        /*

        try {
        	String shellScriptOutput = FasUtils.executeShellScriptFromWindows("app3.galco.com", "sva0604", "TalliBujji1#", "/home/sva0604/ZRunTestIPostFromWin.sh 1825915 10-1");
        	log.debug("Shell script output: " + shellScriptOutput);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        System.exit(0);

        try {
			// int rc = executeCommandLine("/home/sva0604/ZShellTest2.sh", 12000);
			int rc = executeCommandLine("/apps/wds/GALCO/batch/FasIP", 12000);
			System.out.println(rc);
		} catch (IOException | InterruptedException | TimeoutException e3) {
			e3.printStackTrace();
		}
        System.exit(0);

        try {
			int rc = executeCommandLine("cmd /k start \"\" C:\\0Ati\\ZTest.bat", 12000);
			log.debug(rc);
		} catch (IOException | InterruptedException | TimeoutException e3) {
			e3.printStackTrace();
		}
        System.exit(0);

        try {
        	System.out.println(executeShellScriptFromWindows("app3.galco.com", "sva0604", "TalliBujji1#", "/home/sva0604/ZShellTest.sh"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        System.exit(0);




    	// System.out.println(roundTo2Decimals(3.456789));
    	// System.exit(0);


    	// System.out.println(prependZeros(null, 5));
    	// System.exit(0);

        */

    	/*
    	HashMap<String, Object> hm = new HashMap<String, Object>(20);
    	hm.put("a", "a");
    	hm.put("b", "b");
    	hm.put("c", new Integer(3));
    	hm.put("d", new Double(4.0));

    	System.out.println(buildSQLInsertStatement(hm, "SomeTable"));
    	System.exit(0);
		*/


    	/*
		// System.out.println(3 & 4);
		System.out.println("***" + transformData((Integer) null) + "***");
		System.exit(0);
		*/


		/*
		ArrayList<WDSFascorRequestCommon> wdsFascorRequestCommonAL = new ArrayList<WDSFascorRequestCommon>(100);

		try {
			wdsFascorRequestCommonAL.add(buildWDSFascorRequest(
					"201802081230242808166|N|1110C|Thu Feb  8 12:30:24 2018      1825881"));
			wdsFascorRequestCommonAL.add(buildWDSFascorRequest(
					"201802081230242808166|N|0140C|Thu Feb  8 12:30:24 2018      17236"));
			wdsFascorRequestCommonAL.add(buildWDSFascorRequest(
					"201802081230242808166|N|1110C|Thu Feb  8 12:30:24 2018      1825882"));
			wdsFascorRequestCommonAL.add(buildWDSFascorRequest(
					"201802081230242808166|N|1110A|Thu Feb  8 12:30:24 2018      1825882"));
			wdsFascorRequestCommonAL.add(buildWDSFascorRequest(
					"201802081230242808166|N|0140C|Thu Feb  8 12:30:24 2018      17235"));
			wdsFascorRequestCommonAL.add(buildWDSFascorRequest(
					"201802081230242808166|N|1110A|Thu Feb  8 12:30:24 2018      1825881"));
			wdsFascorRequestCommonAL.add(buildWDSFascorRequest(
					"201802081230242808166|N|1210C|Thu Feb  8 12:30:24 2018      PO23554"));
			wdsFascorRequestCommonAL.add(buildWDSFascorRequest(
					"201802081230242808166|N|1210A|Thu Feb  8 12:30:24 2018      PO23554"));
			wdsFascorRequestCommonAL.add(buildWDSFascorRequest(
					"201802081230242808166|N|1220C|Thu Feb  8 12:30:24 2018      PO23554,2"));
			wdsFascorRequestCommonAL.add(buildWDSFascorRequest(
					"201802081230242808166|N|1220A|Thu Feb  8 12:30:24 2018      PO23554,2"));
			wdsFascorRequestCommonAL.add(buildWDSFascorRequest(
					"201802081230242808166|N|1220C|Thu Feb  8 12:30:24 2018      PO23554,1"));
			wdsFascorRequestCommonAL.add(buildWDSFascorRequest(
					"201802081230242808166|N|1220A|Thu Feb  8 12:30:24 2018      PO23554,1"));
			wdsFascorRequestCommonAL.add(buildWDSFascorRequest(
					"201802081230242808166|N|0140A|Thu Feb  8 12:30:24 2018      17236"));
			wdsFascorRequestCommonAL.add(buildWDSFascorRequest(
					"201802081230242808166|N|0140A|Thu Feb  8 12:30:24 2018      17235"));

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		Collections.sort(wdsFascorRequestCommonAL);

		for (Iterator iterator = wdsFascorRequestCommonAL.iterator(); iterator.hasNext();) {
			WDSFascorRequestCommon wdsFascorRequestCommon = (WDSFascorRequestCommon) iterator.next();
			wdsFascorRequestCommon.print();
		}

		System.exit(0);

		*/

		/*
		for (int i = 0; i < Message_1110.fieldsHM.size(); i++) {
			System.out.println(Message_1110.fieldsHM.get(i) + " " + Message_1110.startingPosHM.get(i) + " " + Message_1110.lengthsHM.get(i));
		}
		System.exit(0);
		*/

		/*
		HashMap<String, String> dataHM = new HashMap<String, String>(100);
	    String textString0 = "000000000000000000000000000000000000000000000000000000000";
	    String textString1 = "111111111111111111111111111111111111111111111111111111111";
		String message = "";
		for (int i = 0; i < Message_1110.fieldsHM.size(); i++) {
			dataHM.put(Message_1110.fieldsHM.get(i), (((i % 2) == 0)?textString0:textString1));
		}

		Message_1110 message_1110 = new Message_1110(dataHM);

		Utils.printMessageFields(message_1110);

		String fascorMessageStr = Utils.buildFascorMessage(message_1110);
		*/

	}

	public static String getOutboundMessageTextSubstring(String text, int begin, int length) {
		return text.substring(begin - 1, begin - 1 + length);
	}

	public static void main2(String[] args) {
		String[] fiftyByteStringArray = splitInto50ByteStrings("123456789012345678901234567890", "1234567890123 5678901234567890", "123456789012345678901234567890");
		log.debug(fiftyByteStringArray[0]);
		log.debug(fiftyByteStringArray[1]);
		System.exit(0);



		log.debug(convertDateToCCYYMMDD("2013-12-") + "***");
		System.exit(0);

		// Y/N		Converts null to blanks			Y/N
		// Y/N		Convert boolean to Y or N		Y/N
		// Y/N		Convert Number fields to a decimal with 2 decimals and implied decimal
		// Y/N		Trim()?

		// log.debug("***" + transformData(new Boolean(true)) + "***");
		// log.debug("***" + transformData(new String("abc "), "Y NN") + "***");
		System.exit(0);


		log.debug("***" + DECIMAL_FORMAT_WITH_2_DECIMAL_PLACES.format(new Float("1.234")).replace(".", "") + "***");
		System.exit(0);

		String sequenceNumber = "2018011911040079675858";

		String fascorRequestHour = "", fascorRequestAMPM = "";
		if (new Integer(sequenceNumber.substring(8, 10)) > 12) {
			fascorRequestHour =  "" + (new Integer(sequenceNumber.substring(8, 10)) - 12);
			if (fascorRequestHour.length() < 2) {
				fascorRequestHour = "0" + fascorRequestHour;
			}
			fascorRequestAMPM = "PM";
		} else if (new Integer(sequenceNumber.substring(8, 10)) == 12) {
			fascorRequestHour =  sequenceNumber.substring(8, 10);
			fascorRequestAMPM = "PM";
		} else {
			fascorRequestHour =  sequenceNumber.substring(8, 10);
			fascorRequestAMPM = "AM";
		}
		String fascorRequestTime = sequenceNumber.substring(0, 8) + " " +
								   fascorRequestHour + ":" +
								   sequenceNumber.substring(10, 12) + ":" +
								   sequenceNumber.substring(12, 14) + " " +
								   fascorRequestAMPM;

		log.debug(fascorRequestTime);

		// log.debug(getNumberAsDouble('c'));
		// System.exit(0);
	}
}
