package com.robin.customeView;



import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.robin.backtracking.R;
import com.robin.utils.UtilTools;


public class CustomDialog extends Dialog {
	public Window window;

	public CustomDialog(Context context) {
		super(context);
	}

	public CustomDialog(Context context, int theme) {
		super(context, theme);
		window = getWindow();
	}

	public static class Builder {
		private Context context;
		private String title;
		private String message;
		private String positiveButtonText;
		private String negativeButtonText;
		private View contentView;
		private OnClickListener positiveButtonClickListener;
		private OnClickListener negativeButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 * 
		 * @param
		 * @return
		 */
		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		/**
		 * Set the Dialog title from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 * 
		 * @param title
		 * @return
		 */

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		/**
		 * Set the positive button resource and it's listener
		 * 
		 * @param positiveButtonText
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText,
				OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
				OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int negativeButtonText,
				OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String negativeButtonText,
				OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}
		
		public CustomDialog create() {
			return create(true);
		}

		public CustomDialog create(boolean flag) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomDialog dialog = new CustomDialog(context,R.style.Dialog);
			View layout = inflater.inflate(R.layout.dialoge_view, null);
			/*LayoutParams params = new LayoutParams(Utils.dp2px(context, 280), Utils.dp2px(context, 150));
			
			//dialog.addContentView(layout, params);
			dialog.addContentView(layout, params);*/
			dialog.setContentView(layout);
			
			dialog.setCancelable(flag);
			
			Window window = dialog.window;
			
			WindowManager.LayoutParams params = window.getAttributes();
			//params.width = WindowManager.LayoutParams.WRAP_CONTENT;
			//params.height = WindowManager.LayoutParams.WRAP_CONTENT;
			
			params.width = UtilTools.dp2px(context, 280);
			params.height = UtilTools.dp2px(context, 150);
			params.gravity = Gravity.CENTER;
			window.setAttributes(params);
			
			// set the dialog title
			// set the confirm button
			if (positiveButtonText != null) {
				((Button) layout.findViewById(R.id.positiveButton))
						.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((Button) layout.findViewById(R.id.positiveButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.positiveButton).setVisibility(
						View.GONE);
			}
			// set the cancel button
			if (negativeButtonText != null) {
				((Button) layout.findViewById(R.id.negativeButton))
						.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					((Button) layout.findViewById(R.id.negativeButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									negativeButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.negativeButton).setVisibility(
						View.GONE);
			}
			// set the content message
			if (message != null) {
				((TextView) layout.findViewById(R.id.message)).setText(message);
			} 
			
			return dialog;
		}

	}
}
