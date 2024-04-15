package etu.seinksansdoozebank.dechetri.ui.wastereport;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import etu.seinksansdoozebank.dechetri.R;
import etu.seinksansdoozebank.dechetri.databinding.FragmentWasteReportBinding;
import etu.seinksansdoozebank.dechetri.ui.wastemap.Waste;

public class WasteReportFragment extends Fragment {
    private FragmentWasteReportBinding binding;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Intent> takePictureLauncher;

    Button validateButton;
    private Uri photoUri;

    private NavController navController;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_waste_report, container, false);

        //Si on appuie sur le bouton annuler alors on reviens en arrière.
        binding.cancelButton.setOnClickListener(view1 -> requireActivity().onBackPressed());



         pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        // Le code ici sera exécuté lorsque l'utilisateur aura sélectionné une image
                        Intent data = result.getData();
                        // Récupérer l'URI de l'image sélectionnée
                        if (data != null) {
                            validateButton.setEnabled(true);
                            photoUri = data.getData();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Aucune image sélectionnée", Toast.LENGTH_SHORT).show();
                    }
                });

        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        validateButton.setEnabled(true);
                        Intent data = result.getData();
                        Bitmap photoBitmap = (Bitmap) data.getExtras().get("data");
                        if (photoBitmap != null) {
                            convertBitmapToUri(photoBitmap);
                        }
                    } else {
                        Toast.makeText(requireContext(), "Aucune photo capturée", Toast.LENGTH_SHORT).show();
                    }
                });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        validateButton = view.findViewById(R.id.confirmButton);
        validateButton.setEnabled(false);


        //Si on clique sur le bouton de la pellicule alors on ouvre le service du téléphone.
        binding.LibraryPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //création d'une intent pour ouvrir la pellicule
                Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (pickPhotoIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    //On lance l'instance créée pour ouvrir la pellicule
                    pickImageLauncher.launch(pickPhotoIntent);
                } else {
                    //Si erreur
                    Toast.makeText(requireContext(), "L'application de galerie n'est pas disponible", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Si on clique sur le bouton de l'appareil photo  alors on ouvre le service du téléphone.
        binding.CameraButton.setOnClickListener(view12 -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                //Créer l'activité pour lancer l'appareil photo
                takePictureLauncher.launch(takePictureIntent);
            } else {
           // si erreur alors afficher un message d'information
                Toast.makeText(requireContext(), "L'application de l'appareil photo n'est pas disponible", Toast.LENGTH_SHORT).show();
            }
        });

        navController = NavHostFragment.findNavController(this);
        //Lors de la validation on créé un déchet avec la photo et tous les autres paramètres nuls
        binding.confirmButton.setOnClickListener(view12 -> {
            Waste waste=new Waste(null,0,0,null,photoUri);
                navController.navigate(R.id.navigation_map); // Naviguer vers le fragment carte
        });

        //go back
        binding.cancelButton.setOnClickListener(view12 -> {
            navController.navigate(R.id.navigation_flux); // Naviguer vers le fragment carte
        });


    }

    private void convertBitmapToUri(Bitmap bitmap) {
        // Créez un répertoire de stockage où on enregistre l'image
        File imagesDir = new File(requireActivity().getExternalFilesDir(null), "images");
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }

        // Créez un fichier dans le repertoire avec un nom de reférence unique
        String fileName = "image_" + System.currentTimeMillis() + ".jpg";
        File imageFile = new File(imagesDir, fileName);

        try {
            // écriture du Bitmap dans le fichier en utilisant un OutputStream
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            // enregistrement de ce bitmap en format Uri
            photoUri=Uri.fromFile(imageFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}