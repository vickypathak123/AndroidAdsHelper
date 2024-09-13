# Exit Dialog Usage

A material exit dialog for Android based on `BottomSheetDialog`.

## UI

<img src="https://github.com/vickypathak123/AndroidAdsHelper/blob/main/screenshots/exit_dialog_with_ad.jpg" height="auto" width="300" alt="Exit Dialog With Native Ad"/>    <img src="https://github.com/vickypathak123/AndroidAdsHelper/blob/main/screenshots/exit_dialog_without_ad.jpg" height="auto" width="300" alt="Exit Dialog With Exit Icon"/>

## Usage

- Create an `ExitDialog` object in your activity or fragment file, for example:

    ```kotlin
        class YourActivity : AppCompatActivity() {
        
            private val mExitDialog: ExitDialog by lazy {
                /**
                 * Class for [ExitDialog]s styled as a bottom sheet.
                 *
                 * @param fActivity it refers to your [ComponentActivity] context.
                 * @param isForTesting [by Default value = false] it's refers to UI change like test exit Ad's & exit Icon.
                 * @param backgroundColor it's refers to main background color of dialog.
                 * @param iconColor it's refers to exit icon color of dialog.
                 * @param iconLineColor it's refers to exit icon background line color of dialog.
                 * @param titleTextColor it's refers to title text color of dialog.
                 * @param subTitleTextColor it's refers to sub title text color of dialog.
                 * @param buttonTextColor it's refers to positive & negative button text color of dialog.
                 * @param buttonBackgroundColor it's refers to positive & negative button background color of dialog.
                 * @param buttonStrokeColor it's refers to positive & negative button background stroke color of dialog.
                 */
                ExitDialog(
                    fActivity = mActivity,
                    isForTesting = false,
                    backgroundColor = R.color.exit_background_color,
                    iconColor = R.color.exit_icon_color,
                    iconLineColor = R.color.exit_icon_line_color,
                    titleTextColor = R.color.exit_title_text_color,
                    subTitleTextColor = R.color.exit_sub_title_text_color,
                    buttonTextColor = R.color.exit_button_text_color,
                    buttonBackgroundColor = R.color.exit_button_background_color,
                    buttonStrokeColor = R.color.exit_button_stroke_color
                )
            }
        }
    ```

- Show an `ExitDialog` using above object in your activity or fragment file, for example:

    ```kotlin
        class YourActivity : AppCompatActivity() {
            fun backForExit() {
                /**
                 * Show the exit dialog.
                 *
                 * @param fLanguageCode it's refers to your app language code.
                 * @param subTitleId it's refers to your exit dialog sub title string resources id.
                 */
                mExitDialog.show(
                    fLanguageCode = "en",
                    subTitleId = R.string.exit_dialog_sub_title
                )
            }
        }
    ```