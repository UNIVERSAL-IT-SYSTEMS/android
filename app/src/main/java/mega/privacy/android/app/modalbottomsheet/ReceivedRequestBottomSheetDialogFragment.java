package mega.privacy.android.app.modalbottomsheet;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import mega.privacy.android.app.DatabaseHandler;
import mega.privacy.android.app.MegaApplication;
import mega.privacy.android.app.R;
import mega.privacy.android.app.components.RoundedImageView;
import mega.privacy.android.app.lollipop.ManagerActivityLollipop;
import mega.privacy.android.app.lollipop.controllers.ContactController;
import mega.privacy.android.app.utils.Constants;
import mega.privacy.android.app.utils.Util;
import nz.mega.sdk.MegaApiAndroid;
import nz.mega.sdk.MegaContactRequest;

public class ReceivedRequestBottomSheetDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    Context context;
    MegaContactRequest request = null;
    ContactController cC;

    private BottomSheetBehavior mBehavior;

    public LinearLayout mainLinearLayout;
    public TextView titleNameContactChatPanel;
    public TextView titleMailContactChatPanel;
    public RoundedImageView contactImageView;
    public TextView avatarInitialLetter;
    public LinearLayout optionAccept;
    public LinearLayout optionIgnore;
    public LinearLayout optionDecline;

    DisplayMetrics outMetrics;

    MegaApiAndroid megaApi;
    DatabaseHandler dbH;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (megaApi == null){
            megaApi = ((MegaApplication) ((Activity)context).getApplication()).getMegaApi();
        }

        if(context instanceof ManagerActivityLollipop){
            request = ((ManagerActivityLollipop) context).getSelectedRequest();
        }

        cC = new ContactController(context);

        dbH = DatabaseHandler.getDbHandler(getActivity());
    }
    @Override
    public void setupDialog(final Dialog dialog, int style) {

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.received_request_bottom_sheet, null);

        mainLinearLayout = (LinearLayout) contentView.findViewById(R.id.received_request_item_bottom_sheet);

        titleNameContactChatPanel = (TextView) contentView.findViewById(R.id.received_request_list_contact_name_text);
        titleMailContactChatPanel = (TextView) contentView.findViewById(R.id.received_request_list_contact_mail_text);
        contactImageView = (RoundedImageView) contentView.findViewById(R.id.sliding_received_request_list_thumbnail);
        avatarInitialLetter = (TextView) contentView.findViewById(R.id.sliding_received_request_list_initial_letter);
        optionAccept = (LinearLayout) contentView.findViewById(R.id.contact_list_option_accept_layout);
        optionIgnore= (LinearLayout) contentView.findViewById(R.id.contact_list_option_ignore_layout);
        optionDecline= (LinearLayout) contentView.findViewById(R.id.contact_list_option_decline_layout);

        titleNameContactChatPanel.setMaxWidth(Util.scaleWidthPx(200, outMetrics));
        titleMailContactChatPanel.setMaxWidth(Util.scaleWidthPx(200, outMetrics));

        optionAccept.setOnClickListener(this);
        optionIgnore.setOnClickListener(this);
        optionDecline.setOnClickListener(this);

        titleNameContactChatPanel.setText(request.getSourceEmail());
        titleMailContactChatPanel.setText(""+ DateUtils.getRelativeTimeSpanString(request.getCreationTime() * 1000));

        addAvatarRequestPanel(request);

        dialog.setContentView(contentView);
    }

    public void addAvatarRequestPanel(MegaContactRequest request){

        ////DEfault AVATAR
        Bitmap defaultAvatar = Bitmap.createBitmap(Constants.DEFAULT_AVATAR_WIDTH_HEIGHT, Constants.DEFAULT_AVATAR_WIDTH_HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(defaultAvatar);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(getResources().getColor(R.color.lollipop_primary_color));

        int radius;
        if (defaultAvatar.getWidth() < defaultAvatar.getHeight())
            radius = defaultAvatar.getWidth() / 2;
        else
            radius = defaultAvatar.getHeight() / 2;

        c.drawCircle(defaultAvatar.getWidth() / 2, defaultAvatar.getHeight() / 2, radius, p);
        contactImageView.setImageBitmap(defaultAvatar);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        String fullName = request.getSourceEmail();

        if (fullName != null) {
            if (fullName.length() > 0) {
                if (fullName.trim().length() > 0) {
                    String firstLetter = fullName.charAt(0) + "";
                    firstLetter = firstLetter.toUpperCase(Locale.getDefault());
                    avatarInitialLetter.setText(firstLetter);
                    avatarInitialLetter.setTextColor(Color.WHITE);
                    avatarInitialLetter.setVisibility(View.VISIBLE);
                    avatarInitialLetter.setTextSize(22);
                } else {
                    avatarInitialLetter.setVisibility(View.INVISIBLE);
                }
            }
            else{
                avatarInitialLetter.setVisibility(View.INVISIBLE);
            }

        } else {
            avatarInitialLetter.setVisibility(View.INVISIBLE);
        }

        ////
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.contact_list_option_accept_layout:{
                log("click Accept");
                if(request==null){
                    log("Selected request NULL");
                    return;
                }
                cC.acceptInvitationContact(request);
                break;
            }
            case R.id.contact_list_option_ignore_layout:{
                log("optionIgnore");
                if(request==null){
                    log("Selected request NULL");
                    return;
                }
                cC.ignoreInvitationContact(request);
                break;
            }
            case R.id.contact_list_option_decline_layout:{
                log("optionDecline");
                if(request==null){
                    log("Selected request NULL");
                    return;
                }
                cC.declineInvitationContact(request);
                break;
            }
        }

//        dismiss();
        mBehavior = BottomSheetBehavior.from((View) mainLinearLayout.getParent());
        mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private static void log(String log) {
        Util.log("ReceivedRequestBottomSheetDialogFragment", log);
    }
}
