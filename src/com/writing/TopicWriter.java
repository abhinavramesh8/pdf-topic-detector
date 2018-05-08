package com.writing;

import com.io.CSVHandler;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitWidthDestination;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 29/04/18.
 */
public class TopicWriter {
    private final PDFont FONT = PDType1Font.TIMES_ROMAN;
    private final String WORD_DELIM = ",";
    private final float MARGIN = 72;
    private final float FONT_SIZE = 16;
    private final float LEADING = 1.5f * FONT_SIZE;

    private File file;
    private PDDocument document;

    public TopicWriter(File PDF) {
        this.file = PDF;
    }

    public PDDocument getDoc() {
        return document;
    }

    public void setDoc(PDDocument doc) {
        this.document = doc;
    }

    private String romanNumeral(int num) {
        try {
            String rom[] = {"i", "ii", "iii", "iv", "v", "vi", "vii", "viii", "ix", "x"};
            return rom[num - 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            return String.valueOf(num);
        }
    }

    private List<String> getLines(List<String> texts, float width) throws IOException {
        List<String> lines = new ArrayList<String>();
        int textNum = 1;
        for (String text : texts) {
            text = text.replaceAll(WORD_DELIM, " ");
            text = romanNumeral(textNum) + ") " + text;
            textNum++;
            int lastSpace = -1;
            while (text.length() > 0) {
                int spaceIndex = text.indexOf(' ', lastSpace + 1);
                if (spaceIndex < 0)
                    spaceIndex = text.length();
                String subString = text.substring(0, spaceIndex);
                float size = FONT_SIZE * FONT.getStringWidth(subString) / 1000;
                if (size > width) {
                    if (lastSpace < 0)
                        lastSpace = spaceIndex;
                    subString = text.substring(0, lastSpace);
                    lines.add(subString);
                    text = text.substring(lastSpace).trim();
                    lastSpace = -1;
                } else if (spaceIndex == text.length()) {
                    lines.add(text);
                    text = "";
                } else {
                    lastSpace = spaceIndex;
                }
            }
        }
        return lines;
    }

    private void addLink(PDPage page, float coordinates[][], int destPage) throws IOException {
        PDDocument doc = getDoc();
        PDPageDestination destination = new PDPageFitWidthDestination();
        PDActionGoTo action = new PDActionGoTo();
        PDAnnotationLink link = new PDAnnotationLink();
        PDRectangle position = new PDRectangle();
        position.setLowerLeftX(coordinates[0][0]);
        position.setLowerLeftY(coordinates[0][1]);
        position.setUpperRightX(coordinates[1][0]);
        position.setUpperRightY(coordinates[1][1]);
        link.setRectangle(position);
        destination.setPage(doc.getPage(destPage));
        action.setDestination(destination);
        link.setAction(action);
        PDBorderStyleDictionary borderULine = new PDBorderStyleDictionary();
        borderULine.setStyle(PDBorderStyleDictionary.STYLE_UNDERLINE);
        borderULine.setWidth(0.0f);
        link.setBorderStyle(borderULine);
        page.getAnnotations().add(link);
    }

    private void addPageLink(PDPage page, String word, float coordinates[][]) throws IOException {
        PDDocument doc = getDoc();
        int destPageNum;
        try {
            destPageNum = Integer.parseInt(word);
            addLink(page, coordinates, destPageNum);
        } catch (NumberFormatException e) {

        }
    }

    private float writeWord(PDPage page, String word, PDPageContentStream contentStream, float currX, float coordinates[][], boolean space) throws IOException {
        contentStream.showText(word);
        float spaceWidth = FONT_SIZE * FONT.getStringWidth(" ") / 1000;
        float currXOffset = FONT_SIZE * FONT.getStringWidth(word) / 1000;
        currXOffset = space ? currXOffset + spaceWidth : currXOffset;
        coordinates[0][0] = currX;
        currX += currXOffset;
        coordinates[1][0] = currX - spaceWidth;
        addPageLink(page, word, coordinates);
        contentStream.newLineAtOffset(currXOffset, 0);
        return currX;
    }

    private void writeLine(PDPage page, PDPageContentStream contentStream, String line, float lowerY) throws IOException {
        String words[] = line.split(" ");
        float currX = MARGIN;
        int numWords = words.length - 1;
        float coordinates[][] = new float[2][2];
        coordinates[0][1] = lowerY + LEADING;
        coordinates[1][1] = coordinates[0][1] + FONT_SIZE;
        for (int i = 0; i < numWords; i++) {
            currX = writeWord(page, words[i], contentStream, currX, coordinates, true);
        }
        currX = writeWord(page, words[numWords], contentStream, currX, coordinates, false);
        float lineWidth = FONT_SIZE * FONT.getStringWidth(line) / 1000;
        contentStream.newLineAtOffset(-lineWidth, -LEADING);
    }

    public void write(List<String> texts, String savePath) throws IOException {
        PDDocument doc = PDDocument.load(this.file);
        setDoc(doc);
        try {
            PDPage page = new PDPage();
            PDPageTree pdPageTree = doc.getPages();
            PDPage nextPage = pdPageTree.get(0);
            pdPageTree.insertBefore(page, nextPage);
            PDPageContentStream contentStream = new PDPageContentStream(doc, page);
            PDRectangle mediabox = page.getMediaBox();
            float width = mediabox.getWidth() - 2 * MARGIN;
            float startX = mediabox.getLowerLeftX() + MARGIN;
            float startY = mediabox.getUpperRightY() - MARGIN;
            List<String> lines = getLines(texts, width);
            contentStream.beginText();
            float indFontSize = 22;
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, indFontSize);
            contentStream.newLineAtOffset(startX, startY);
            contentStream.showText("Index:");
            contentStream.endText();
            startY -= 1.5f * indFontSize;
            contentStream.beginText();
            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.newLineAtOffset(startX, startY);
            float currentY = startY;
            for (String line : lines) {
                currentY -= LEADING;
                if (currentY <= MARGIN) {
                    contentStream.endText();
                    contentStream.close();
                    page = new PDPage();
                    pdPageTree.insertBefore(page, nextPage);
                    contentStream = new PDPageContentStream(doc, page);
                    contentStream.beginText();
                    contentStream.setFont(FONT, FONT_SIZE);
                    contentStream.newLineAtOffset(startX, startY);
                    currentY = startY;
                    currentY -= LEADING;
                }
                writeLine(page, contentStream, line, currentY);
            }
            contentStream.endText();
            contentStream.close();
            doc.save(savePath);
        } finally {
            if (doc != null) {
                doc.close();
            }
        }
    }

    public void write(CSVHandler csvHandler, String savePath) throws IOException {
        write(csvHandler.readLines(), savePath);
    }

}
