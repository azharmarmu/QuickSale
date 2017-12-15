package azhar.com.quicksale.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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

import azhar.com.quicksale.R;

import static android.content.Context.DOWNLOAD_SERVICE;


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
    public void generateSalesReport(Activity activity, Task<QuerySnapshot> salesTask, String currentRoute, String date) {
        try {
            DialogUtils.showProgressDialog(activity, activity.getString(R.string.loading));
            File pictureFileDir = new File(Environment.getExternalStorageDirectory(), "QS_POS/reports");
            if (!pictureFileDir.exists()) {
                boolean isDirectoryCreated = pictureFileDir.mkdirs();
                if (!isDirectoryCreated)
                    Log.i("ATG", "Can't create directory to save the image");
            }

            if (currentRoute == null || currentRoute.isEmpty() || currentRoute.equals("")) {
                currentRoute = Constants.ALL;
            }

            currentRoute = currentRoute.replace("/", "-");
            date = date.replace("/", "-");

            String filename = pictureFileDir.getPath() + File.separator + currentRoute + "_" + date + ".pdf";
            File pdfFile = new File(filename);
            pdfFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pdfFile);
            Document document = new Document();
            PdfWriter.getInstance(document, oStream);

            document.open();
            Paragraph paragraph = new Paragraph("SALES REPORT", catFont);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);

            paragraph = new Paragraph(currentRoute + ":" + date, catFont);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            paragraph = new Paragraph("\n");
            document.add(paragraph);

            int cashAmount = 0;
            int totalBill = 0;
            for (final DocumentSnapshot documentSnapshot : salesTask.getResult()) {
                HashMap<String, Object> items = (HashMap<String, Object>) documentSnapshot.get(Constants.BILL_SALES);
                HashMap<String, Object> customer = (HashMap<String, Object>) documentSnapshot.get(Constants.BILL_CUSTOMER);


                String billNo = documentSnapshot.get(Constants.BILL_NO).toString();
                String billDate = documentSnapshot.get(Constants.BILL_DATE).toString();
                String billRoute = documentSnapshot.get(Constants.BILL_ROUTE).toString();
                String billAmountReceived = "0";
                if (documentSnapshot.contains(Constants.BILL_AMOUNT_RECEIVED)) {
                    billAmountReceived = documentSnapshot.get(Constants.BILL_AMOUNT_RECEIVED).toString();
                    cashAmount += Integer.parseInt(billAmountReceived);
                }
                String billNetTotal = "0";
                if (documentSnapshot.contains(Constants.BILL_NET_TOTAL)) {
                    billNetTotal = documentSnapshot.get(Constants.BILL_NET_TOTAL).toString();
                    totalBill += Integer.parseInt(billNetTotal);
                }
                /*Header*/
                String customerDetails = customer.get(Constants.CUSTOMER_NAME).toString() + "\n" +
                        customer.get(Constants.CUSTOMER_GST).toString();

                document.add(createHeaderTable(billNo, customerDetails, billDate));

                /*Body*/
                for (String itemName : items.keySet()) {
                    HashMap<String, Object> itemDetails = (HashMap<String, Object>) items.get(itemName);
                    document.add(createBodyTable(
                            itemDetails.get(Constants.BILL_SALES_PRODUCT_NAME).toString(),
                            itemDetails.get(Constants.BILL_SALES_PRODUCT_QTY).toString(),
                            itemDetails.get(Constants.BILL_SALES_PRODUCT_RATE).toString(),
                            itemDetails.get(Constants.BILL_SALES_PRODUCT_TOTAL).toString()));
                }

                /*Footer*/
                document.add(createFooterTable(Integer.parseInt(billNetTotal), Integer.parseInt(billAmountReceived)
                ));
            }


            document.add(paragraph);
            document.add(createTotalTable(totalBill, cashAmount));
            document.close();
            Log.e("FileGenerated", currentRoute + "-" + date + ".pdf");

            DialogUtils.dismissProgressDialog();
            DialogUtils.appToastShort(activity, "Report generated!!");

            DownloadManager downloadManager = (DownloadManager) activity.getSystemService(DOWNLOAD_SERVICE);

            assert downloadManager != null;
            downloadManager.addCompletedDownload(pdfFile.getName(), pdfFile.getName(),
                    true, "application/pdf",
                    pdfFile.getAbsolutePath(), pdfFile.length(),
                    true);

            oStream.flush();
            oStream.close();

        } catch (Exception e) {
            DialogUtils.appToastShort(activity, "Report generating failed");
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
