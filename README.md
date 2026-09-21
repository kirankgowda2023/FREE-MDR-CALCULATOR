# 💳 MDR Calculator

### Calculate payment charges. Understand your settlement. Optimize your payment costs.

MDR Calculator is a lightweight financial utility designed for local merchants, shop owners, restaurants, pharmacies, salons, service providers, and small businesses.

The app helps merchants calculate **Merchant Discount Rate (MDR)**, GST on MDR, fixed payment fees, and the final amount they will receive after payment processing charges.

It also includes a **QR Code Generator with payment splitting**, allowing merchants to structure payments across different payment methods or transaction amounts where appropriate.

---

## 🚀 Features

### 🧮 MDR Calculator

Calculate the actual cost of accepting digital payments.

Enter:

- Transaction amount
- Payment method
- MDR percentage
- Fixed transaction fee
- GST on MDR

The calculator provides:

- MDR amount
- GST on MDR
- Fixed charges
- Total payment charges
- Net settlement amount
- Effective payment processing cost

### Example

For a ₹10,000 transaction:

| Component | Amount |
|---|---:|
| Transaction Amount | ₹10,000 |
| MDR @ 1.50% | ₹150 |
| GST on MDR @ 18% | ₹27 |
| Fixed Fee | ₹0 |
| **Total Charges** | **₹177** |
| **Net Settlement** | **₹9,823** |

---

# 📱 QR Code Generator

The app also includes a QR Code Generator designed to help merchants create payment QR codes and plan payment splitting.

### Key capabilities

- Generate payment QR codes
- Enter merchant/payment details
- Specify payment amount
- Split a payment into multiple transactions
- Calculate the MDR impact of each transaction
- Compare payment charges
- Save/share generated QR codes

### Example

Instead of treating a ₹10,000 payment as one transaction, the merchant can explore a split such as:

```text
Total Payment
₹10,000

Payment 1
₹5,000

Payment 2
₹5,000
````

The application can calculate the corresponding payment processing cost based on the selected MDR structure.

> **Important:** Actual MDR, transaction limits, UPI/card charges, taxes, and payment-provider rules vary by provider and transaction type. The calculator provides estimates based on the values entered by the user and should not be treated as a guarantee of actual settlement charges.

---

# 🎯 Why This App?

Small merchants often know how much the customer paid but may not immediately know how much they actually receive after payment processing charges.

For example:

```text
Customer pays
₹10,000
      ↓
MDR
₹150
      ↓
GST on MDR
₹27
      ↓
Total Charges
₹177
      ↓
Merchant receives
₹9,823
```

MDR Calculator makes this calculation simple and transparent.

---

# 🧑‍💼 Target Users

The application is designed for:

* 🏪 Retail shops
* 🍽️ Restaurants
* 💊 Pharmacies
* 💇 Salons
* 🛠️ Service providers
* 🛒 Small businesses
* 👨‍💼 Freelancers
* 🧑‍🔧 Local professionals
* 🏬 Small and medium merchants

---

# 🧩 Core Modules

```text
MDR Calculator
       │
       ├── Transaction Amount
       ├── Payment Method
       ├── MDR %
       ├── Fixed Fee
       ├── GST
       │
       ↓
Payment Cost Calculation
       │
       ├── MDR
       ├── GST
       ├── Total Charges
       └── Net Settlement


QR Code Generator
       │
       ├── Payment Details
       ├── Amount
       ├── Payment Split
       ├── QR Generation
       └── Share / Save
```

---

# 💡 Payment Split Feature

The QR payment split feature is designed to help merchants understand how transaction structure can affect payment processing costs.

Example:

```text
Single Transaction

₹20,000
MDR @ 1.5%
MDR = ₹300
```

The merchant can then explore different payment splits and compare the resulting estimated fees.

The app should be used to **compare legitimate payment structures**, not to circumvent payment-provider rules, transaction limits, or applicable regulations.

Always follow the terms and conditions of the relevant payment provider.

---

# 🛠️ Technology

> Update this section with your actual stack.

Example:

* Flutter
* Dart
* Material Design
* QR Code generation library
* Local state management
* Responsive mobile UI

If your implementation uses different technologies, replace the above with the actual stack.

---

# 📂 Project Structure

Example:

```text
lib/
│
├── main.dart
│
├── screens/
│   ├── home/
│   ├── mdr_calculator/
│   ├── qr_generator/
│   └── settings/
│
├── widgets/
│   ├── amount_input.dart
│   ├── fee_breakdown.dart
│   ├── result_card.dart
│   ├── payment_selector.dart
│   └── qr_preview.dart
│
├── models/
│   ├── transaction.dart
│   └── payment_split.dart
│
├── services/
│   ├── mdr_calculator.dart
│   └── qr_service.dart
│
└── utils/
    └── currency_formatter.dart
```

Adjust this structure to match the actual repository.

---

# 🧮 MDR Calculation Logic

The basic calculation is:

```text
MDR Amount = Transaction Amount × MDR %

GST on MDR = MDR Amount × GST %

Total Charges =
MDR Amount + GST on MDR + Fixed Fee

Net Settlement =
Transaction Amount - Total Charges
```

Example:

```text
Transaction = ₹10,000
MDR = 1.5%
GST = 18%

MDR:
₹10,000 × 1.5%
= ₹150

GST:
₹150 × 18%
= ₹27

Total Charges:
₹150 + ₹27
= ₹177

Net Settlement:
₹10,000 - ₹177
= ₹9,823
```

---

# 🔐 Privacy

The application is designed with simplicity and privacy in mind.

Where supported by the implementation:

* No unnecessary user account
* Minimal data collection
* No requirement to store sensitive financial information
* Calculations can be performed locally
* QR/payment information should be handled carefully

Do not enter sensitive banking credentials, card PINs, passwords, or authentication information into the application.

---

# ⚠️ Disclaimer

MDR Calculator is an informational calculation tool.

Actual payment processing charges may vary depending on:

* Payment method
* Card type
* Merchant category
* Payment provider
* Acquirer
* Transaction value
* Applicable taxes
* Commercial agreements
* Current payment-provider policies

The values displayed by the application are estimates based on user-provided inputs.

The QR/payment-splitting feature does not guarantee lower fees or successful payment processing.

Users should comply with applicable laws, payment-provider terms, transaction limits, tax requirements, and merchant agreements.

---

# 📸 Screenshots

Add application screenshots here.

Example:

```text
/screenshots/
├── home.png
├── mdr-calculator.png
├── calculation-result.png
├── qr-generator.png
└── payment-split.png
```

Then add them to the README:

```markdown
## Screenshots

### MDR Calculator

![MDR Calculator](screenshots/mdr-calculator.png)

### QR Generator

![QR Generator](screenshots/qr-generator.png)

### Payment Split

![Payment Split](screenshots/payment-split.png)
```

---

# 🚀 Getting Started

## Prerequisites

Install the required development environment for the technology used by this project.

For Flutter:

```bash
flutter --version
```

Clone the repository:

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
```

Navigate to the project:

```bash
cd <PROJECT_FOLDER>
```

Install dependencies:

```bash
flutter pub get
```

Run the application:

```bash
flutter run
```

---

# 🧪 Testing

Run:

```bash
flutter test
```

For static analysis:

```bash
flutter analyze
```

Build Android APK:

```bash
flutter build apk
```

---

# 🗺️ Future Roadmap

Potential future improvements:

* [ ] Save calculation history
* [ ] Merchant profile
* [ ] Multiple MDR profiles
* [ ] Monthly MDR cost calculator
* [ ] Payment-provider comparison
* [ ] QR payment history
* [ ] Expense tracking
* [ ] Monthly payment analytics
* [ ] Export reports
* [ ] PDF reports
* [ ] Multi-language support
* [ ] Dark mode
* [ ] Offline-first improvements
* [ ] Merchant-specific fee presets

---

# 🤝 Contributing

Contributions, suggestions, and improvements are welcome.

1. Fork the repository
2. Create a feature branch

```bash
git checkout -b feature/your-feature
```

3. Commit your changes

```bash
git commit -m "Add your feature"
```

4. Push the branch

```bash
git push origin feature/your-feature
```

5. Open a Pull Request

---

# 📄 License

Add your preferred license here.

Example:

```text
MIT License
```

---

# 👨‍💻 Author

**Kiran Kumar K**

Business Analyst | Data Consultant | Product & Technology Enthusiast

LinkedIn:
[https://www.linkedin.com/in/kirankumark07/](https://www.linkedin.com/in/kirankumark07/)

---

## ⭐ Support

If you find this project useful, consider giving the repository a ⭐ on GitHub.

Suggestions and feedback are welcome.

```

### One change I'd make to the product positioning

Instead of calling it only **“MDR Calculator,”** consider a broader product name such as:

**MDR Saver**  
*Calculate. Split. Save.*

or

**MerchantPay Tools**  
*Understand your payment costs.*

The reason is that your **QR generator + payment-splitting functionality gives you room to expand beyond a calculator** into a small merchant payment-cost utility.
```
