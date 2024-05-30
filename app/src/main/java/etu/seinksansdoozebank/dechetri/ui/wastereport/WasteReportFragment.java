package etu.seinksansdoozebank.dechetri.ui.wastereport;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import etu.seinksansdoozebank.dechetri.R;


public class WasteReportFragment extends Fragment {
    private static final int CAMERA_PERMISSION_CODE = 100 ;
    private static final int LIBRARY_PERMISSION_CODE =200 ;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Intent> takePictureLauncher;

    private ImageView imageView;

    Button validateButton;
    private byte[] chosenImage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_waste_report, container, false);

        //Si on appuie sur le bouton annuler alors on revient en arrière.
        imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(0);


         pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Le code ici sera exécuté lorsque l'utilisateur aura sélectionné une image
                        Intent data = result.getData();

                        // Récupérer l'URI de l'image sélectionnée
                        if (data != null) {
                            imageView.setImageURI(data.getData());
                            validateButton.setEnabled(true);
                            try {
                                chosenImage = convertUriToByte(data.getData());
                            } catch (IOException e) {
                               throw new RuntimeException(e);
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), R.string.aucune_image_selectionnee, Toast.LENGTH_SHORT).show();
                    }
                });

        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        validateButton.setEnabled(true);
                        Intent data = result.getData();
                        Bitmap photoBitmap = (Bitmap) data.getExtras().get("data");
                        if (photoBitmap != null) {
                            imageView.setImageBitmap(photoBitmap);
                            try {
                                chosenImage = convertBitmapToByte(photoBitmap);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), R.string.aucune_photo_capturee, Toast.LENGTH_SHORT).show();
                    }
                });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        validateButton = view.findViewById(R.id.confirmButton);
        validateButton.setEnabled(false);


        //Si on clique sur le bouton de la pellicule alors on ouvre le service du téléphone.
        view.findViewById(R.id.LibraryPhoto).setOnClickListener(view1 -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                selectPictureFromGallery();
            } else {
                // Si la permission n'est pas accordée, demandez la permission
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, LIBRARY_PERMISSION_CODE);
            }
        });

        //Si on clique sur le bouton de l'appareil photo  alors on ouvre le service du téléphone.
        view.findViewById(R.id.CameraButton).setOnClickListener(view12 -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                takePictureWithCamera();
            } else {
                // Si la permission n'est pas accordée, demandez la permission
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            }
        });


        //Lors de la validation on créé un déchet avec la photo et tous les autres paramètres nuls
        view.findViewById(R.id.confirmButton).setOnClickListener(v -> {
            Bundle bundle=new Bundle();
            bundle.putByteArray("image",chosenImage);
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_navigation_report_to_navigation_location_choice,bundle);
        });

    }

  private byte[] convertBitmapToByte(Bitmap bitmapImage) throws IOException{
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
      return stream.toByteArray();
  }

    private byte[] convertUriToByte(Uri uri) throws IOException {
        InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, bytesRead);
        }
        return byteBuffer.toByteArray();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case CAMERA_PERMISSION_CODE :
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePictureWithCamera();
                } else {
                    // Permission is denied, show a message or handle accordingly
                    Toast.makeText(requireContext(), R.string.permission_de_la_camera_refusee, Toast.LENGTH_SHORT).show();
                }
            case LIBRARY_PERMISSION_CODE : // Si on a la permission de la pellicule
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectPictureFromGallery();
                } else {
                    // Permission is denied, show a message or handle accordingly
                    Toast.makeText(requireContext(), R.string.permission_de_la_pellicule_refusee, Toast.LENGTH_SHORT).show();
                }
            default :
                Toast.makeText(requireContext(), R.string.permission_refusee, Toast.LENGTH_SHORT).show();
        }
    }

    private void takePictureWithCamera(){
        // Si la permission est accordée, lancez l'intent pour ouvrir l'appareil photo
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            // Créez l'activité pour lancer l'appareil photo
            takePictureLauncher.launch(takePictureIntent);
        } else {
            // si erreur alors afficher un message d'information
            Toast.makeText(requireContext(), R.string.l_application_de_l_appareil_photo_n_est_pas_disponible, Toast.LENGTH_SHORT).show();
        }
    }

    private void selectPictureFromGallery(){
        // Si la permission est accordée, lancez l'intent pour ouvrir la galerie
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (pickPhotoIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            // Lancez l'intent pour ouvrir la galerie
            pickImageLauncher.launch(pickPhotoIntent);
        } else {
            // si erreur alors afficher un message d'information
            Toast.makeText(requireContext(), R.string.l_application_de_galerie_n_est_pas_disponible, Toast.LENGTH_SHORT).show();
        }
    }



}