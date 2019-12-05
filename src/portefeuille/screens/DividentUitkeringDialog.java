package portefeuille.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.sql.DataSource;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

import portefeuille.tables.DividentUitkeringenList;
import portefeuille.util.ColumnsAutoSizer;
import portefeuille.util.DataTableModel;

public class DividentUitkeringDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	EffectenFrame theEFrame;
	
	final Dimension dim = new Dimension(460,520);
	
	DataSource ds;

	public DividentUitkeringDialog(EffectenFrame theParent)
	{
		super(theParent,"Divident Uitekeringen Vooruitzicht");
		theEFrame = theParent;
		ds = theParent.getDs();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		DividentUitkeringenList divUitKeringList = new DividentUitkeringenList(ds);
		DataTableModel model = new DataTableModel(divUitKeringList.getTableData(),divUitKeringList.getColumnNames());
		JTable table = new JTable(model);
		table.setSelectionBackground(Color.LIGHT_GRAY);
		ColumnsAutoSizer as = new ColumnsAutoSizer();
		as.sizeColumnsToFit(table);
		
		JScrollPane scrollPane = new JScrollPane(table,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		table.setFillsViewportHeight(true);
		scrollPane.setAutoscrolls(true);
		scrollPane.setMinimumSize(dim);
		scrollPane.setPreferredSize(dim);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 0,5));
		this.add(scrollPane, BorderLayout.CENTER);

  	JPanel buttonPane  = new JPanel();
  	buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0,0));
  	
    JButton button = new JButton("Ok");
    button.setMaximumSize(new Dimension(70, 25));
    button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
						setVisible(false);
						dispose(); 
			}});
    buttonPane.add(button,BorderLayout.EAST);
    this.add(buttonPane,BorderLayout.PAGE_END);    
		setSize(dim);
//		setMinimumSize(dim);
//		setMaximumSize(dim);
		setLocationRelativeTo(null);

		setVisible(true);
	}
}
