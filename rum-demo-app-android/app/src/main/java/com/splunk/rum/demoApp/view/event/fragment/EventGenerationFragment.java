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
import com.splunk.rum.demoApp.callback.ViewListener;
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
    FragmentEventGenerationBinding binding;
    private EventViewModel viewModel;
    private ViewListener viewListener;


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

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCrashApp:
                Integer.parseInt("");
                break;
            case R.id.btnANR:
                int i = 0;
                //noinspection InfiniteLoopStatement
                while (true) {
                    i++;
                }
            case R.id.btnGenerateException:
                try {
                    int a = 10;
                    int b = 0;
                    long c = a / b;
                } catch (Exception e) {
                    AppUtils.handleRumException(e);
                }
                break;
            case R.id.btnFreezeApp:
                // TODO need to confirm if custom event needs to be sent for the below use case
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
                }
                break;
            case R.id.btnSlowApiResponse:
                viewModel.slowApiResponse();
                break;
            case R.id.btnHttpNotFound:
                viewModel.generateHttpNotFound();
                break;
            case R.id.btnHttpError:
                viewModel.generateHttpError();
                break;
            case R.id.btnShopWebView:
                NavHostFragment.findNavController(EventGenerationFragment.this).navigate(R.id.action_navigation_events_to_navigation_shop_web_view);
                break;
            case R.id.btnLocalWebView:
                NavHostFragment.findNavController(EventGenerationFragment.this).navigate(R.id.action_navigation_events_to_local_web_view);
                break;
            default:
                break;
        }
    }


}