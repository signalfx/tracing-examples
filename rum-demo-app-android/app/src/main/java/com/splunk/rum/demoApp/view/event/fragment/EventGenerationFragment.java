package com.splunk.rum.demoApp.view.event.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.splunk.rum.SplunkRum;
import com.splunk.rum.demoApp.R;
import com.splunk.rum.demoApp.databinding.FragmentEventGenerationBinding;
import com.splunk.rum.demoApp.util.AppUtils;
import com.splunk.rum.demoApp.util.ResourceProvider;
import com.splunk.rum.demoApp.view.base.activity.BaseActivity;
import com.splunk.rum.demoApp.view.base.fragment.BaseFragment;
import com.splunk.rum.demoApp.view.base.viewModel.ViewModelFactory;
import com.splunk.rum.demoApp.view.event.viewModel.EventViewModel;
import com.splunk.rum.demoApp.view.home.MainActivity;

import io.opentelemetry.api.trace.Span;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventGenerationFragment} factory method to
 * create an instance of this fragment.
 */
public class EventGenerationFragment extends BaseFragment implements View.OnClickListener, LifecycleObserver {
    private EventViewModel viewModel;
    @SuppressWarnings("ALL")
    private FragmentEventGenerationBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEventGenerationBinding.inflate(inflater, container, false);
        if (getActivity() != null && getActivity() instanceof BaseActivity &&
                ((MainActivity) getActivity()).getBottomNavigationView() != null) {
            ((MainActivity) getActivity()).getBottomNavigationView().getMenu().findItem(R.id.navigation_events).setChecked(true);
        }

        // Configure ViewModel
        viewModel = new ViewModelProvider(this, new ViewModelFactory(new ResourceProvider(getResources()))).get(EventViewModel.class);
        viewModel.createView(this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        viewModel.getmIsLoading().observe(getActivity(), handleLoadingResponse());
        viewModel.setIsLoading(false);

        // Set OnClick Listener
        binding.btnCrashApp.setOnClickListener(this);
        binding.btnFreezeApp.setOnClickListener(this);
        binding.btnANR.setOnClickListener(this);
        binding.btnGenerateException.setOnClickListener(this);
        binding.btnSlowApiResponse.setOnClickListener(this);
        binding.btnHttpError.setOnClickListener(this);
        binding.btnHttpNotFound.setOnClickListener(this);
        binding.btnShopWebView.setOnClickListener(this);
        binding.btnLocalWebView.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        AppUtils.enableDisableBtn(true,binding.btnLocalWebView);
        AppUtils.enableDisableBtn(true,binding.btnShopWebView);
    }

    /**
     * @return show hider progressbar based on  isLoading boolean value
     */

    private androidx.lifecycle.Observer<Boolean> handleLoadingResponse() {
        return isLoading -> {
            if(!isLoading){
                AppUtils.enableDisableBtn(true,binding.btnSlowApiResponse);
                AppUtils.enableDisableBtn(true,binding.btnHttpError);
                AppUtils.enableDisableBtn(true,binding.btnHttpNotFound);
            }

            try {
                AppUtils.showHideLoader(isLoading,binding.progressBar.progressLinearLayout,null);
            } catch (Exception e) {
                AppUtils.handleRumException(e);
            }
        };
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCrashApp:
                throw new RuntimeException(getString(R.string.rum_event_crash_manually));
            case R.id.btnANR:
                int i = 0;
                //noinspection InfiniteLoopStatement
                while (true) {
                    i++;
                }
            case R.id.btnGenerateException:
                AppUtils.enableDisableBtn(false,binding.btnGenerateException);
                try {
                    throw new RuntimeException(getString(R.string.rum_event_exception_manually));
                } catch (Exception e) {
                    AppUtils.handleRumException(e);
                    AppUtils.enableDisableBtn(true,binding.btnGenerateException);
                }
                break;
            case R.id.btnFreezeApp:
                AppUtils.enableDisableBtn(false,binding.btnFreezeApp);
                Span appFreezer = SplunkRum.getInstance().startWorkflow(getString(R.string.rum_event_app_freezer));
                try {
                    for (int j = 0; j < 20; j++) {
                        Thread.sleep(1000);
                        appFreezer.addEvent(getString(R.string.rum_event_sleep_mode));
                    }
                } catch (InterruptedException e) {
                    AppUtils.handleRumException(e);
                    e.printStackTrace();
                } finally {
                    appFreezer.end();
                    AppUtils.enableDisableBtn(true,binding.btnFreezeApp);
                }
                break;
            case R.id.btnSlowApiResponse:
                AppUtils.enableDisableBtn(false,binding.btnSlowApiResponse);
                viewModel.slowApiResponse();
                break;
            case R.id.btnHttpNotFound:
                AppUtils.enableDisableBtn(false,binding.btnHttpNotFound);
                viewModel.generateHttpNotFound();
                break;
            case R.id.btnHttpError:
                AppUtils.enableDisableBtn(false,binding.btnHttpError);
                viewModel.generateHttpError("",0);
                break;
            case R.id.btnShopWebView:
                AppUtils.enableDisableBtn(false,binding.btnShopWebView);
                NavHostFragment.findNavController(EventGenerationFragment.this).navigate(R.id.action_navigation_events_to_navigation_shop_web_view);
                break;
            case R.id.btnLocalWebView:
                AppUtils.enableDisableBtn(false,binding.btnLocalWebView);
                NavHostFragment.findNavController(EventGenerationFragment.this).navigate(R.id.action_navigation_events_to_local_web_view);
                break;
            default:
                break;
        }
    }

    public EventViewModel getViewModel() {
        return viewModel;
    }
}