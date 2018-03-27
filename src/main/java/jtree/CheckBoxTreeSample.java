/*
 * #%L
 * Swing JTree check box nodes.
 * %%
 * Copyright (C) 2012 - 2017 Board of Regents of the University of
 * Wisconsin-Madison.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package jtree;

import massCrawl.CrawlMain;
import massCrawl.SubCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Curtis Rueden
 * @author John Zukowski
 * @author Alde
 */
public class CheckBoxTreeSample {

    private static final Logger logger = LoggerFactory.getLogger(CrawlMain.class);


    public static void main(final String args[]) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e1) {
            e1.printStackTrace();
        }

        logger.info("Chargement des catégories et sous catégories en tant qu'arbre...");

        final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");

        try {
            Map<String, List<SubCategory>> categoryHashMap = CrawlMain.run();

            for (Map.Entry<String, List<SubCategory>> pair : categoryHashMap.entrySet()) {

                String categoryName = pair.getKey();
                List<SubCategory> subCat = pair.getValue();

                final DefaultMutableTreeNode category = new DefaultMutableTreeNode(categoryName);

                for (SubCategory subCategory : subCat) {

                    DefaultMutableTreeNode e = new DefaultMutableTreeNode(new SubCategoryNodeData(subCategory));
                    category.add(e);
                }

                root.add(category);
            }

            final JTree tree = new JTree(root);

            final SubCategoryNodeRenderer renderer = new SubCategoryNodeRenderer();
            tree.setCellRenderer(renderer);

            final SubCategoryNodeEditor editor = new SubCategoryNodeEditor(tree);
            tree.setCellEditor(editor);
            tree.setEditable(true);

            // listen for changes in the selection
            tree.addTreeSelectionListener(new TreeSelectionListener() {
                @Override
                public void valueChanged(final TreeSelectionEvent e) {
                    System.out.println(System.currentTimeMillis() + ": selection changed");
                }
            });

            // listen for changes in the model (including check box toggles)
            tree.getModel().addTreeModelListener(new TreeModelListener() {

                @Override
                public void treeNodesChanged(final TreeModelEvent e) {
                    System.out.println(System.currentTimeMillis() + ": nodes changed");
                }

                @Override
                public void treeNodesInserted(final TreeModelEvent e) {
                    System.out.println(System.currentTimeMillis() + ": nodes inserted");
                }

                @Override
                public void treeNodesRemoved(final TreeModelEvent e) {
                    System.out.println(System.currentTimeMillis() + ": nodes removed");
                }

                @Override
                public void treeStructureChanged(final TreeModelEvent e) {
                    System.out.println(System.currentTimeMillis() + ": structure changed");
                }
            });

            // show the tree onscreen
            final JFrame frame = new JFrame("CheckBox Tree");
            final JScrollPane scrollPane = new JScrollPane(tree);
            frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(300, 150);
            frame.setVisible(true);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
