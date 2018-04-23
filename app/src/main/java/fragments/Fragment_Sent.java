package fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.projects.shrungbhatt.filetransfer.R;
import com.projects.shrungbhatt.filetransfer.adpaters.Adapter_HistoryListItem;
import com.projects.shrungbhatt.filetransfer.utils.Utility;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class Fragment_Sent extends Fragment {


    Unbinder unbinder;
    @BindView(R.id.sent_recycler_view)
    RecyclerView mSentRecyclerView;
    private List<String> mFiles;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sent, container, false);
        unbinder = ButterKnife.bind(this, v);


        mFiles = Utility.getFiles("sent");

        mSentRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));

        mSentRecyclerView.setAdapter(new Adapter_HistoryListItem(getActivity(),mFiles));


        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
