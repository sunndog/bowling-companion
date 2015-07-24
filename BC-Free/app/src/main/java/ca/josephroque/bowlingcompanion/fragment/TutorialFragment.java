package ca.josephroque.bowlingcompanion.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.theme.Theme;
import ca.josephroque.bowlingcompanion.utilities.DataFormatter;

/**
 * Created by Joseph Roque on 2015-07-24. Displays some content which describes the functionality of
 * the application to the user.
 */
public class TutorialFragment
        extends Fragment
        implements Theme.ChangeableTheme
{

    /** To identify output from this class in the Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "SplashActivity";

    /** Represents page of the tutorial displayed by this fragment. */
    private static final String ARG_PAGE = "arg_page";

    /** Number of pages in the tutorial. */
    public static final int TUTORIAL_TOTAL_PAGES = 6;
    /** Vertical padding between views. */
    private static final int VERTICAL_SPACE = 16;
    /** Size of the text in tutorials. */
    private static final int TUTORIAL_TEXT_SIZE_SP = 16;

    /**
     * A cached array of the {@link
     * ca.josephroque.bowlingcompanion.fragment.TutorialFragment.TutorialPage} items.
     */
    private static TutorialPage[] sTutorialPageValues;

    /** Displays layout to showcase tutorial step. */
    private View mViewTutorial;
    /** Explains the tutorial step. */
    private TextView mTextViewTutorial;

    /** Page of the tutorial shown in this fragment. */
    private TutorialPage mTutorialPage;
    /** Indicates if the current device is a tablet. */
    private boolean mIsTablet = false;

    /**
     * Creates a new instance of TutorialFragment showing the page defined.
     *
     * @param page tutorial page to show
     * @return a new instance of TutorialFragment
     */
    public static TutorialFragment newInstance(int page)
    {
        if (page < 0 || page >= TUTORIAL_TOTAL_PAGES)
            throw new IllegalArgumentException("page must be between 0 and "
                    + (TUTORIAL_TOTAL_PAGES - 1));

        TutorialFragment instance = new TutorialFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mIsTablet = !getResources().getBoolean(R.bool.portrait_only);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        RelativeLayout rootView =
                (RelativeLayout) inflater.inflate(R.layout.fragment_tutorial, container, false);

        Bundle args = (savedInstanceState == null)
                ? getArguments()
                : savedInstanceState;

        if (sTutorialPageValues == null)
            sTutorialPageValues = TutorialPage.values();
        mTutorialPage = sTutorialPageValues[args.getInt(ARG_PAGE)];

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        setupTutorialPageAndText((RelativeLayout) getView());
        updateTheme();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_PAGE, mTutorialPage.ordinal());
    }

    @Override
    public void updateTheme()
    {
        if (getView() != null)
            getView().setBackgroundColor(Theme.getTertiaryThemeColor());
    }

    /**
     * Initializes the text and image objects for the tutorial.
     *
     * @param rootView root to attach views to
     */
    private void setupTutorialPageAndText(RelativeLayout rootView)
    {
        final int dp16 = DataFormatter.getPixelsFromDP(getResources().getDisplayMetrics().density,
                VERTICAL_SPACE);

        mTextViewTutorial = new TextView(getActivity());
        mTextViewTutorial.setId(R.id.tv_tutorial);
        mTextViewTutorial.setPadding(dp16, dp16, dp16, dp16);
        mTextViewTutorial.setGravity(Gravity.CENTER_HORIZONTAL);
        mTextViewTutorial.setTextSize(TypedValue.COMPLEX_UNIT_SP, TUTORIAL_TEXT_SIZE_SP);
        mTextViewTutorial.setTextColor(getResources().getColor(android.R.color.white));

        switch (mTutorialPage)
        {
            case ADD_BOWLER_LEAGUE_SERIES:
                mTextViewTutorial.setText(R.string.text_tutorial_add_bowler_league_series);
                mViewTutorial = View.inflate(getActivity(), R.layout.tutorial_add, null);
                setTutorialLayout(rootView);
                break;
            case LEAGUES_VS_EVENTS:
                mTextViewTutorial.setText(R.string.text_tutorial_leagues_events);
                mViewTutorial = View.inflate(getActivity(), R.layout.tutorial_leagues_events, null);
                setTutorialLayout(rootView);
                break;
            case PIN_GAME_AND_SWIPE:
                mTextViewTutorial.setText(R.string.text_tutorial_pin_game);
                mViewTutorial = View.inflate(getActivity(), R.layout.tutorial_pin_game, null);
                setTutorialLayout(rootView);
                break;
            case MANUAL_GAME:
                mTextViewTutorial.setText(R.string.text_tutorial_manual_game);
                mViewTutorial = View.inflate(getActivity(), R.layout.tutorial_manual_game, null);
                setTutorialLayout(rootView);
                break;
            case STATISTICS_AND_GRAPH:
                mTextViewTutorial.setText(R.string.text_tutorial_statistics);
                mViewTutorial = View.inflate(getActivity(), R.layout.tutorial_statistics, null);
                setTutorialLayout(rootView);
                break;
            case SETTINGS:
                mTextViewTutorial.setText(R.string.text_tutorial_settings);
                mViewTutorial = View.inflate(getActivity(), R.layout.tutorial_settings, null);
                setTutorialLayout(rootView);
                break;
            default:
                throw new IllegalStateException("invalid tutorial page: " + mTutorialPage);
        }
    }

    /**
     * Sets the layout to alternate between two setups depending on the page.
     *
     * @param rootView root to attach views to
     */
    private void setTutorialLayout(RelativeLayout rootView)
    {
        final float maxTutorialWidth = getResources().getDimension(R.dimen.max_tutorial_width);
        RelativeLayout.LayoutParams layoutParams;
        rootView.removeAllViews();

        final int dp16 = DataFormatter.getPixelsFromDP(getResources().getDisplayMetrics().density,
                VERTICAL_SPACE);

        if (mViewTutorial != null)
        {
            if (mIsTablet)
                layoutParams = new RelativeLayout.LayoutParams((int) maxTutorialWidth,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            else
                layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(dp16, dp16, dp16, dp16);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            rootView.addView(mViewTutorial, layoutParams);

            if (mIsTablet)
                layoutParams = new RelativeLayout.LayoutParams((int) maxTutorialWidth,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            else
                layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ABOVE, R.id.tutorial_content);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            rootView.addView(mTextViewTutorial, layoutParams);
        }
        else
        {

            if (mIsTablet)
                layoutParams = new RelativeLayout.LayoutParams((int) maxTutorialWidth,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            else
                layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            rootView.addView(mTextViewTutorial, layoutParams);
        }
    }

    /**
     * Possible tutorial pages.
     */
    public enum TutorialPage
    {
        /** Tutorial page for adding new bowlers, leagues and series. */
        ADD_BOWLER_LEAGUE_SERIES,
        /** Tutorial page for leagues vs events. */
        LEAGUES_VS_EVENTS,
        /** Tutorial page for recording a game with pins. */
        PIN_GAME_AND_SWIPE,
        /** Tutorial page for recording a game with only the score. */
        MANUAL_GAME,
        /** Tutorial page for viewing statistics and graphs. */
        STATISTICS_AND_GRAPH,
        /** Tutorial page for changing settings. */
        SETTINGS,
    }
}