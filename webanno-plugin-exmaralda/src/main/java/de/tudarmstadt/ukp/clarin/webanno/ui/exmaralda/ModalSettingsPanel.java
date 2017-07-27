package de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda;

import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

public class ModalSettingsPanel extends Panel
{

	private static final long serialVersionUID = 1L;


	public ModalSettingsPanel(String id, final ModalWindow window)
	{
		super(id);

		int width = ExmaraldaPartitur.getTableWidth();
	    TextField<Integer> fwidth = new TextField<Integer>("fwidth", Model.of(width));

	    add((new Form<Void>("widthForm") {
	        private static final long serialVersionUID = 2445612544114726143L;

	        @Override
	        protected void onSubmit() {
	        	int submittedWidth = fwidth.getModelObject();
	        	Session.get().setAttribute(ExmaraldaPartitur.SESSION_PARAM_TABLE_WIDTH, submittedWidth);
	        }

	    }).add(fwidth).add(new AjaxSubmitLink("fsubmit") {

			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onAfterSubmit(AjaxRequestTarget target, Form<?> form) {
				super.onAfterSubmit(target, form);
				
				window.close(target);
			}
			
		}));
	}

}
