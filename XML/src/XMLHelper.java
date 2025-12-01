package com.psyche.xml;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XMLHelper {
    // Простой путь к XML файлу
    private static final String XML_PATH = "library.xml";

    public static List<Book> loadBooks() {
        List<Book> books = new ArrayList<>();

        try {
            File xmlFile = new File(XML_PATH);

            if (!xmlFile.exists()) {
                System.out.println("XML файл не найден: " + xmlFile.getAbsolutePath());
                System.out.println("Создаем пустой список книг");
                return books;
            }

            System.out.println("Загружаем XML из: " + xmlFile.getAbsolutePath());

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("book");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element bookElement = (Element) nodeList.item(i);
                Book book = new Book();

                book.setId(Integer.parseInt(bookElement.getAttribute("id")));
                book.setCopies(Integer.parseInt(bookElement.getAttribute("copies")));
                book.setAvailable(Integer.parseInt(bookElement.getAttribute("available")));

                book.setTitle(getTagValue(bookElement, "title"));
                book.setAuthor(getTagValue(bookElement, "author"));
                book.setYear(Integer.parseInt(getTagValue(bookElement, "year")));
                book.setPrice(Double.parseDouble(getTagValue(bookElement, "price")));
                book.setCategory(getTagValue(bookElement, "category"));

                books.add(book);
            }

            System.out.println("Успешно загружено книг: " + books.size());

        } catch (Exception e) {
            System.err.println("Ошибка при загрузке XML: " + e.getMessage());
            e.printStackTrace();
        }

        return books;
    }

    public static void saveBooks(List<Book> books) {
        try {
            File xmlFile = new File(XML_PATH);
            System.out.println("Сохраняем XML в: " + xmlFile.getAbsolutePath());

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("library");
            doc.appendChild(root);

            for (Book book : books) {
                Element bookElement = doc.createElement("book");
                bookElement.setAttribute("id", String.valueOf(book.getId()));
                bookElement.setAttribute("copies", String.valueOf(book.getCopies()));
                bookElement.setAttribute("available", String.valueOf(book.getAvailable()));

                bookElement.appendChild(createElement(doc, "title", book.getTitle()));
                bookElement.appendChild(createElement(doc, "author", book.getAuthor()));
                bookElement.appendChild(createElement(doc, "year", String.valueOf(book.getYear())));
                bookElement.appendChild(createElement(doc, "price", String.valueOf(book.getPrice())));
                bookElement.appendChild(createElement(doc, "category", book.getCategory()));

                root.appendChild(bookElement);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);

            transformer.transform(source, result);

            System.out.println("Данные успешно сохранены!");

        } catch (Exception e) {
            System.err.println("Ошибка при сохранении XML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getTagValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            if (node != null && node.getChildNodes().getLength() > 0) {
                return node.getChildNodes().item(0).getNodeValue();
            }
        }
        return "";
    }

    private static Element createElement(Document doc, String name, String value) {
        Element element = doc.createElement(name);
        element.appendChild(doc.createTextNode(value));
        return element;
    }
}