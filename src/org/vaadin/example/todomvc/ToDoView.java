package org.vaadin.example.todomvc;

import java.util.HashSet;
import java.util.Set;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TextField;

public class ToDoView extends CssLayout {
    private TextField newTodo;
    private CssLayout main;
    private CheckBox toggleAll;

    private boolean newTodoFocused;
    private Set<TodoRow> rows = new HashSet<TodoRow>();

    public ToDoView() {
        setSizeUndefined();
        setId("todoapp");

        Label header = new Label("<h1>todos</h1>", ContentMode.HTML);
        header.setId("header");
        header.setSizeUndefined();
        addComponent(header);

        newTodo = new TextField();
        newTodo.setId("new-todo");
        newTodo.setInputPrompt("What needs to be done?");
        addComponent(newTodo);

        addComponent(main = new CssLayout() {
            {
                setId("main");
                toggleAll = new CheckBox("Mark all as complete");
                toggleAll.setId("toggle-all");
                addComponent(toggleAll);
            }
        });

        toggleAll.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                boolean completed = toggleAll.getValue().booleanValue();
                for (TodoRow row : rows) {
                    row.setCompleted(completed);
                }
            }
        });

        newTodo.addFocusListener(new FocusListener() {

            @Override
            public void focus(FocusEvent event) {
                newTodoFocused = true;
            }
        });
        newTodo.addBlurListener(new BlurListener() {

            @Override
            public void blur(BlurEvent event) {
                newTodoFocused = false;
            }
        });
        addShortcutListener(new ShortcutListener(null, KeyCode.ENTER, null) {

            @Override
            public void handleAction(Object sender, Object target) {
                if (newTodoFocused) {
                    String value = newTodo.getValue();
                    if (!value.isEmpty()) {
                        main.addComponent(getNewTodoRow(value, false, false));
                        newTodo.setValue("");
                    }
                } else {
                    getUI().focus();
                }
            }
        });

        addComponent(new CssLayout() {
            {
                setId("footer");

                Label todoCount = new Label("<b>##</b> items left",
                        ContentMode.HTML);
                todoCount.setId("todo-count");
                todoCount.setSizeUndefined();
                addComponent(todoCount);

                CssLayout filters = new CssLayout();
                filters.setId("filters");
                addComponent(filters);

                NativeButton all = new NativeButton("All");
                all.addStyleName("selected");
                NativeButton active = new NativeButton("Active");
                NativeButton completed = new NativeButton("Completed");
                filters.addComponents(all, active, completed);

                NativeButton clearCompleted = new NativeButton(
                        "Clear completed (#)");
                clearCompleted.setId("clear-completed");
                addComponent(clearCompleted);

            }
        });
    }

    CssLayout getNewTodoRow(final String captionText, final boolean done,
            final boolean editing) {
        TodoRow row = new TodoRow(captionText, done, editing);
        rows.add(row);
        return row;
    }

    private class TodoRow extends CssLayout {
        private CheckBox checkbox;

        public TodoRow(final String captionText, final boolean done,
                final boolean editing) {
            addStyleName("todo-row");
            if (done) {
                addStyleName("completed");
            }
            if (editing) {
                addStyleName("editing");
            }

            checkbox = new CheckBox(null, done);
            addComponent(checkbox);

            final Label caption = new Label(captionText);
            caption.setSizeUndefined();
            addComponent(caption);

            NativeButton destroy = new NativeButton();
            destroy.addStyleName("destroy");
            addComponent(destroy);

            final TextField edit = new TextField();
            edit.setValue(captionText);
            addComponent(edit);

            checkbox.addValueChangeListener(new ValueChangeListener() {

                @Override
                public void valueChange(ValueChangeEvent event) {
                    if (checkbox.getValue().booleanValue()) {
                        addStyleName("completed");
                    } else {
                        removeStyleName("completed");
                    }
                }
            });

            addLayoutClickListener(new LayoutClickListener() {

                @Override
                public void layoutClick(LayoutClickEvent event) {
                    if (event.isDoubleClick()
                            && caption == event.getClickedComponent()) {
                        addStyleName("editing");
                        edit.selectAll();
                        edit.focus();
                    }
                }
            });

            edit.addBlurListener(new BlurListener() {

                @Override
                public void blur(BlurEvent event) {
                    removeStyleName("editing");
                    caption.setValue(edit.getValue());
                }
            });

            destroy.addClickListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    main.removeComponent(TodoRow.this);
                    rows.remove(TodoRow.this);
                }
            });
        }

        public void setCompleted(boolean completed) {
            checkbox.setValue(completed);
        }
    }
}
