package marmu.com.quicksale.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;


/**
 * Created by azharuddin on 31/10/17.
 */

@SuppressWarnings("unchecked")
public class GenerateSalesReport {

    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL, BaseColor.RED);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void generateSalesReport(Context context,
                                    String routeName, String date,
                                    int cashAmount, int totalBill,
                                    HashMap<String, Object> partyCustomerName,
                                    HashMap<String, Object> partiesNetTotal,
                                    HashMap<String, Object> partiesItems,
                                    HashMap<String, Object> partiesItemsRate,
                                    HashMap<String, Object> partiesItemsTotal,
                                    HashMap<String, Object> partiesBillNo,
                                    HashMap<String, Object> partiesBillDate,
                                    HashMap<String, Object> partiesGST,
                                    HashMap<String, Object> partiesAmountReceived) {
        try {
            File pictureFileDir = new File(Environment.getExternalStorageDirectory(), "QS_POS/reports");
            if (!pictureFileDir.exists()) {
                boolean isDirectoryCreated = pictureFileDir.mkdirs();
                if (!isDirectoryCreated)
                    Log.i("ATG", "Can't create directory to save the image");
            }
            routeName = routeName.replace("/", "-");
            date = date.replace("/", "-");
            String filename = pictureFileDir.getPath() + File.separator + routeName + "_" + date + ".pdf";
            File pdfFile = new File(filename);
            pdfFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pdfFile);
            Document document = new Document();
            PdfWriter.getInstance(document, oStream);

            document.open();
            Paragraph paragraph = new Paragraph("SALES REPORT", catFont);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);

            paragraph = new Paragraph(routeName + ":" + date, catFont);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);

            paragraph = new Paragraph("\n");
            document.add(paragraph);
            for (String key : partyCustomerName.keySet()) {
                document.add(createHeaderTable(
                        partiesBillNo.get(key).toString(),
                        partyCustomerName.get(key).toString() + "\n" + partiesGST.get(key).toString(),
                        partiesBillDate.get(key).toString()
                ));
                HashMap<String, Object> itemsQty = (HashMap<String, Object>) partiesItems.get(key);
                HashMap<String, Object> itemsRate = (HashMap<String, Object>) partiesItemsRate.get(key);
                HashMap<String, Object> itemsTotal = (HashMap<String, Object>) partiesItemsTotal.get(key);
                for (String itemName : itemsQty.keySet()) {
                    document.add(createBodyTable(
                            itemName,
                            itemsQty.get(itemName).toString(),
                            itemsRate.get(itemName).toString(),
                            itemsTotal.get(itemName).toString()));
                }
                document.add(createFooterTable(
                        Integer.parseInt(partiesNetTotal.get(key).toString()),
                        Integer.parseInt(partiesAmountReceived.get(key).toString())
                ));
            }
            document.add(paragraph);
            document.add(createTotalTable(totalBill, cashAmount));
            document.close();
            Log.e("FileGenerated", routeName + "-" + date + ".pdf");
            DialogUtils.appToastShort(context, "Report generated!!");

            oStream.flush();
            oStream.close();
        } catch (Exception e) {
            DialogUtils.appToastShort(context, "Report generating failed");
            Log.e("Error", e.getMessage());
        }
    }

    private static PdfPTable createHeaderTable(String billNo,
                                               String name,
                                               String date) {
        // a table with three columns
        PdfPTable table = new PdfPTable(3);

        // the cell object
        PdfPCell cell;

        cell = new PdfPCell(new Phrase(billNo, subFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorderWidth(1);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(name, subFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorderWidth(1);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(date, subFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorderWidth(1);
        table.addCell(cell);

        return table;
    }

    private static PdfPTable createBodyTable(String itemName,
                                             String itemQty,
                                             String itemRate,
                                             String itemTotal) {
        // a table with three columns
        PdfPTable table = new PdfPTable(4);

        // the cell object
        PdfPCell cell;

        cell = new PdfPCell(new Phrase(itemName));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(itemQty));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(itemRate));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(itemTotal));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);

        return table;
    }

    private PdfPTable createFooterTable(int netTotal, int amountReceived) {
        // a table with three columns
        PdfPTable table = new PdfPTable(3);

        // the cell object
        PdfPCell cell;

        cell = new PdfPCell(new Phrase("Balance: " + String.valueOf(netTotal - amountReceived), subFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Amount Recv: " + String.valueOf(amountReceived), subFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Total: " + String.valueOf(netTotal), subFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);
        return table;
    }

    private PdfPTable createTotalTable(int netTotal, int amountReceived) {
        // a table with three columns
        PdfPTable table = new PdfPTable(3);

        // the cell object
        PdfPCell cell;

        cell = new PdfPCell(new Phrase("Total Balance \n" + String.valueOf(netTotal - amountReceived), subFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Total Amount Recv \n" + String.valueOf(amountReceived), subFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Total Sales \n" + String.valueOf(netTotal), subFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        return table;
    }

    private static void createList(Section subCatPart) {
        List list = new List(true, false, 10);
        list.add(new ListItem("First point"));
        list.add(new ListItem("Second point"));
        list.add(new ListItem("Third point"));
        subCatPart.add(list);
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
}
