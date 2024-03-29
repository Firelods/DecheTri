package etu.seinksansdoozebank.dechetri.ui.wastereport;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

public class WasteReportFragment extends Fragment {
    private FragmentWasteReportBinding binding;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Intent> takePictureLauncher;

    Button validateButton;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentWasteReportBinding.inflate(inflater, container, false);

        //Si on appuie sur le bouton annuler alors on reviens en arrière.
        binding.cancelButton.setOnClickListener(view1 -> requireActivity().onBackPressed());



         pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        // Le code ici sera exécuté lorsque l'utilisateur aura sélectionné une image
                        Intent data = result.getData();
                        // Récupérer l'URI de l'image sélectionnée
                        if (data != null) {
                            validateButton.setVisibility(View.VISIBLE);
                            Uri photoUri = data.getData();
                            binding.imageView.setImageURI(photoUri);
                            //TODO : afficher la photo selectionnée dans la view d'après.
                        }
                    } else {
                        Toast.makeText(requireContext(), "Aucune image sélectionnée", Toast.LENGTH_SHORT).show();
                    }
                });

        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                       validateButton.setVisibility(View.VISIBLE);
                        Intent data = result.getData();
                        Bitmap photoBitmap = (Bitmap) data.getExtras().get("data");
                        // Afficher l'image dans l'ImageView
                        if (photoBitmap != null) {
                            Uri photoUri = saveBitmapToStorage(photoBitmap);
                            binding.imageView.setImageURI(photoUri);
                        }
                        //TODO : afficher la photo prise dans la view d'après.
                    } else {
                        Toast.makeText(requireContext(), "Aucune photo capturée", Toast.LENGTH_SHORT).show();
                    }
                });
        return binding.getRoot();
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
        validateButton.setVisibility(View.GONE);

        // Écouteur de clic pour le bouton ou tout autre déclencheur d'événement
        binding.confirmButton.setOnClickListener(view1 -> {
  //TODO : changer de vue et mettre sur waste details report
        });

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
        binding.CameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    //Créer l'activité pour lancer l'appareil photo
                    takePictureLauncher.launch(takePictureIntent);
                } else {
               // si erreur alors afficher un message d'information
                    Toast.makeText(requireContext(), "L'application de l'appareil photo n'est pas disponible", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Uri saveBitmapToStorage(Bitmap bitmap) {
        // Créez un répertoire de stockage où vous souhaitez enregistrer l'image
        File imagesDir = new File(requireActivity().getExternalFilesDir(null), "images");
        if (!imagesDir.exists()) {
            imagesDir.mkdirs(); // Créez le répertoire s'il n'existe pas déjà
        }

        // Créez un fichier dans ce répertoire avec un nom unique
        String fileName = "image_" + System.currentTimeMillis() + ".jpg";
        File imageFile = new File(imagesDir, fileName);

        try {
            // Écrivez le Bitmap dans le fichier en utilisant un OutputStream
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            // Renvoyez l'URI du fichier enregistré
            return Uri.fromFile(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}