/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package documentation;

import java.util.List;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 *
 * @author sindukur
 */
public class EntityExcelUtil {

    static int ObjTypeIndex = 0;
    static int EntityNameIndex = 1;
    static int PackageNameIndex = 2;
    static int EntityTitleIndex = 3;
    static int AttrNameIndex = 4;
    static int AttrTypeIndex = 5;
    static int PKAttrNameIndex = 6;
    static int RelationNameIndex = 7;
    static int RelationEntityIndex = 8;
    static int RelationTypeIndex = 9;
    static int RelFieldNameIndex = 10;

    public static void writeToExcel(WritableWorkbook workbook, String sheetName, int sheetIndex, List<EntityBase> entityList) throws WriteException {
        WritableSheet sheet = workbook.createSheet(sheetName, sheetIndex);
        int row = 0;
        Label label = new Label(ObjTypeIndex, row, "Obj Type");
        sheet.addCell(label);
        label = new Label(EntityNameIndex, row, "Entity Name");
        sheet.addCell(label);
        label = new Label(PackageNameIndex, row, "Package Name");
        sheet.addCell(label);
        label = new Label(EntityTitleIndex, row, "Entity Title");
        sheet.addCell(label);

        label = new Label(AttrNameIndex, row, "Attr Name");
        sheet.addCell(label);
        label = new Label(AttrTypeIndex, row, "Attr Type");
        sheet.addCell(label);
        label = new Label(PKAttrNameIndex, row, "PK Attr Name");
        sheet.addCell(label);
        label = new Label(RelationNameIndex, row, "Relation Name");
        sheet.addCell(label);
        label = new Label(RelationEntityIndex, row, "Relation entity");
        sheet.addCell(label);
        label = new Label(RelationTypeIndex, row, "Relation type");
        sheet.addCell(label);
        label = new Label(RelFieldNameIndex, row, "RelFieldName");
        sheet.addCell(label);

        row++;
        for (EntityBase entity : entityList) {
            label = new Label(ObjTypeIndex, row, "Entity");
            sheet.addCell(label);
            label = new Label(EntityNameIndex, row, entity.getEntityName());
            sheet.addCell(label);
            label = new Label(PackageNameIndex, row, entity.getPackageName());
            sheet.addCell(label);
            label = new Label(EntityTitleIndex, row, entity.getTitle());
            sheet.addCell(label);
            row++;
/*
            for (Attribute attr : entity.getAttr()) {
                label = new Label(ObjTypeIndex, row, "Attr");
                sheet.addCell(label);
                label = new Label(AttrNameIndex, row, attr.getName());
                sheet.addCell(label);
                label = new Label(AttrTypeIndex, row, attr.getType());
                sheet.addCell(label);
                row++;
            }


            for (PrimaryKey pk : entity.getPks()) {
                label = new Label(ObjTypeIndex, row, "PK");
                sheet.addCell(label);
                label = new Label(PKAttrNameIndex, row, pk.getAttrName());
                sheet.addCell(label);

                row++;
            }

*/

            for (Relation rel : entity.getRelations()) {
                label = new Label(ObjTypeIndex, row, "Relation");
                sheet.addCell(label);
                label = new Label(RelationNameIndex, row, rel.getFkName());
                sheet.addCell(label);
                label = new Label(RelationEntityIndex, row, rel.getRelEntity());
                sheet.addCell(label);
                label = new Label(RelationTypeIndex, row, rel.getType());
                sheet.addCell(label);
                row++;


                for (String s : rel.getFieldsName()) {
                    label = new Label(ObjTypeIndex, row, "RelAttribute");
                    sheet.addCell(label);
                    label = new Label(RelFieldNameIndex, row, s);
                    sheet.addCell(label);
                    row++;
                }
            }
            label = new Label(ObjTypeIndex, row, "EndEntity");
                    sheet.addCell(label);
            row++;
        }

    }
}
